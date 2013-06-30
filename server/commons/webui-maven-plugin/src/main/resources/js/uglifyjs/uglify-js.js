/*
 Copyright 2010 (c) Mihai Bazon <mihai.bazon@gmail.com>
 Based on parse-js (http://marijn.haverbeke.nl/parse-js/).

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 * Redistributions of source code must retain the above
 copyright notice, this list of conditions and the following
 disclaimer.

 * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials
 provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER “AS IS” AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.
 */

//convienence function(src, [options]);
function uglify(orig_code, options) {
    options || (options = {});
    var jsp = uglify.parser;
    var pro = uglify.uglify;

    // parse code and get the initial AST
    var ast = jsp.parse(orig_code, options.strict_semicolons);

    if (options.lift_vars) {
        ast = pro.ast_lift_variables(ast, options.mangle_options);
    }

    // get a new AST with mangled names
    ast = pro.ast_mangle(ast, options.mangle_options);

    // get an AST with compression optimizations
    if (options.squeeze) {
        ast = pro.ast_squeeze(ast, options.squeeze_options);
        if (options.squeeze_options.unsafe) {
            ast = pro.ast_squeeze_more(ast);
        }
    }
    // compressed code here
    var result = pro.gen_code(ast, options.gen_options);

    // slit compressed code
    if (options.max_line_length) {
        result = pro.split_lines(result, options.max_line_length)
    }

    return result;
}

uglify.parser = require("./lib/parse-js");
uglify.uglify = require("./lib/process");
uglify.consolidator = require("./lib/consolidator");

module.exports = uglify;