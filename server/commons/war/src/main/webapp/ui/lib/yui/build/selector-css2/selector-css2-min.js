/*
 YUI 3.10.3 (build 2fb5187)
 Copyright 2013 Yahoo! Inc. All rights reserved.
 Licensed under the BSD License.
 http://yuilibrary.com/license/
 */

YUI.add("selector-css2", function (e, t) {
    var n = "parentNode", r = "tagName", i = "attributes", s = "combinator", o = "pseudos", u = e.Selector, a = {_reRegExpTokens: /([\^\$\?\[\]\*\+\-\.\(\)\|\\])/, SORT_RESULTS: !0, _isXML: function () {
        var t = e.config.doc.createElement("div").tagName !== "DIV";
        return t
    }(), shorthand: {"\\#(-?[_a-z0-9]+[-\\w\\uE000]*)": "[id=$1]", "\\.(-?[_a-z]+[-\\w\\uE000]*)": "[className~=$1]"}, operators: {"": function (t, n) {
        return e.DOM.getAttribute(t, n) !== ""
    }, "~=": "(?:^|\\s+){val}(?:\\s+|$)", "|=": "^{val}-?"}, pseudos: {"first-child": function (t) {
        return e.DOM._children(t[n])[0] === t
    }}, _bruteQuery: function (t, n, r) {
        var i = [], s = [], o = u._tokenize(t), a = o[o.length - 1], f = e.DOM._getDoc(n), l, c, h, p;
        if (a) {
            c = a.id, h = a.className, p = a.tagName || "*";
            if (n.getElementsByTagName)c && (n.all || n.nodeType === 9 || e.DOM.inDoc(n)) ? s = e.DOM.allById(c, n) : h ? s = n.getElementsByClassName(h) : s = n.getElementsByTagName(p); else {
                l = n.firstChild;
                while (l)l.tagName && (p === "*" || l.tagName === p) && s.push(l), l = l.nextSibling || l.firstChild
            }
            s.length && (i = u._filterNodes(s, o, r))
        }
        return i
    }, _filterNodes: function (t, n, r) {
        var i = 0, s, o = n.length, a = o - 1, f = [], l = t[0], c = l, h = e.Selector.getters, p, d, v, m, g, y, b, w;
        for (i = 0; c = l = t[i++];) {
            a = o - 1, m = null;
            e:while (c && c.tagName) {
                v = n[a], b = v.tests, s = b.length;
                if (s && !g)while (w = b[--s]) {
                    p = w[1], h[w[0]] ? y = h[w[0]](c, w[0]) : (y = c[w[0]], w[0] === "tagName" && !u._isXML && (y = y.toUpperCase()), typeof y != "string" && y !== undefined && y.toString ? y = y.toString() : y === undefined && c.getAttribute && (y = c.getAttribute(w[0], 2)));
                    if (p === "=" && y !== w[2] || typeof p != "string" && p.test && !p.test(y) || !p.test && typeof p == "function" && !p(c, w[0], w[2])) {
                        if (c = c[m])while (c && (!c.tagName || v.tagName && v.tagName !== c.tagName))c = c[m];
                        continue e
                    }
                }
                a--;
                if (!!g || !(d = v.combinator)) {
                    f.push(l);
                    if (r)return f;
                    break
                }
                m = d.axis, c = c[m];
                while (c && !c.tagName)c = c[m];
                d.direct && (m = null)
            }
        }
        return l = c = null, f
    }, combinators: {" ": {axis: "parentNode"}, ">": {axis: "parentNode", direct: !0}, "+": {axis: "previousSibling", direct: !0}}, _parsers: [
        {name: i, re: /^\uE003(-?[a-z]+[\w\-]*)+([~\|\^\$\*!=]=?)?['"]?([^\uE004'"]*)['"]?\uE004/i, fn: function (t, n) {
            var r = t[2] || "", i = u.operators, s = t[3] ? t[3].replace(/\\/g, "") : "", o;
            if (t[1] === "id" && r === "=" || t[1] === "className" && e.config.doc.documentElement.getElementsByClassName && (r === "~=" || r === "="))n.prefilter = t[1], t[3] = s, n[t[1]] = t[1] === "id" ? t[3] : s;
            r in i && (o = i[r], typeof o == "string" && (t[3] = s.replace(u._reRegExpTokens, "\\$1"), o = new RegExp(o.replace("{val}", t[3]))), t[2] = o);
            if (!n.last || n.prefilter !== t[1])return t.slice(1)
        }},
        {name: r, re: /^((?:-?[_a-z]+[\w-]*)|\*)/i, fn: function (e, t) {
            var n = e[1];
            u._isXML || (n = n.toUpperCase()), t.tagName = n;
            if (n !== "*" && (!t.last || t.prefilter))return[r, "=", n];
            t.prefilter || (t.prefilter = "tagName")
        }},
        {name: s, re: /^\s*([>+~]|\s)\s*/, fn: function (e, t) {
        }},
        {name: o, re: /^:([\-\w]+)(?:\uE005['"]?([^\uE005]*)['"]?\uE006)*/i, fn: function (e, t) {
            var n = u[o][e[1]];
            return n ? (e[2] && (e[2] = e[2].replace(/\\/g, "")), [e[2], n]) : !1
        }}
    ], _getToken: function (e) {
        return{tagName: null, id: null, className: null, attributes: {}, combinator: null, tests: []}
    }, _tokenize: function (t) {
        t = t || "", t = u._parseSelector(e.Lang.trim(t));
        var n = u._getToken(), r = t, i = [], o = !1, a, f, l, c;
        e:do {
            o = !1;
            for (l = 0; c = u._parsers[l++];)if (a = c.re.exec(t)) {
                c.name !== s && (n.selector = t), t = t.replace(a[0], ""), t.length || (n.last = !0), u._attrFilters[a[1]] && (a[1] = u._attrFilters[a[1]]), f = c.fn(a, n);
                if (f === !1) {
                    o = !1;
                    break e
                }
                f && n.tests.push(f);
                if (!t.length || c.name === s)i.push(n), n = u._getToken(n), c.name === s && (n.combinator = e.Selector.combinators[a[1]]);
                o = !0
            }
        } while (o && t.length);
        if (!o || t.length)i = [];
        return i
    }, _replaceMarkers: function (e) {
        return e = e.replace(/\[/g, "\ue003"), e = e.replace(/\]/g, "\ue004"), e = e.replace(/\(/g, "\ue005"), e = e.replace(/\)/g, "\ue006"), e
    }, _replaceShorthand: function (t) {
        var n = e.Selector.shorthand, r;
        for (r in n)n.hasOwnProperty(r) && (t = t.replace(new RegExp(r, "gi"), n[r]));
        return t
    }, _parseSelector: function (t) {
        var n = e.Selector._replaceSelector(t), t = n.selector;
        return t = e.Selector._replaceShorthand(t), t = e.Selector._restore("attr", t, n.attrs), t = e.Selector._restore("pseudo", t, n.pseudos), t = e.Selector._replaceMarkers(t), t = e.Selector._restore("esc", t, n.esc), t
    }, _attrFilters: {"class": "className", "for": "htmlFor"}, getters: {href: function (t, n) {
        return e.DOM.getAttribute(t, n)
    }, id: function (t, n) {
        return e.DOM.getId(t)
    }}};
    e.mix(e.Selector, a, !0), e.Selector.getters.src = e.Selector.getters.rel = e.Selector.getters.href, e.Selector.useNative && e.config.doc.querySelector && (e.Selector.shorthand["\\.(-?[_a-z]+[-\\w]*)"] = "[class~=$1]")
}, "true", {requires: ["selector-native"]});
