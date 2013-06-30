/*
 YUI 3.10.3 (build 2fb5187)
 Copyright 2013 Yahoo! Inc. All rights reserved.
 Licensed under the BSD License.
 http://yuilibrary.com/license/
 */

YUI.add("exec-command", function (e, t) {
    var n = function () {
        n.superclass.constructor.apply(this, arguments)
    }, r = function (t, n, r) {
        var i = this.getInstance(), s = i.config.doc, o = s.selection.createRange(), u = s.queryCommandValue(t), a, f, l, c, h, p, d;
        u && (a = o.htmlText, f = new RegExp(r, "g"), l = a.match(f), l && (a = a.replace(r + ";", "").replace(r, ""), o.pasteHTML('<var id="yui-ie-bs">'), c = s.getElementById("yui-ie-bs"), h = s.createElement("div"), p = s.createElement(n), h.innerHTML = a, c.parentNode !== i.config.doc.body && (c = c.parentNode), d = h.childNodes, c.parentNode.replaceChild(p, c), e.each(d, function (e) {
            p.appendChild(e)
        }), o.collapse(), o.moveToElementText && o.moveToElementText(p), o.select())), this._command(t)
    };
    e.extend(n, e.Base, {_lastKey: null, _inst: null, command: function (e, t) {
        var r = n.COMMANDS[e];
        return r ? r.call(this, e, t) : this._command(e, t)
    }, _command: function (e, t) {
        var n = this.getInstance();
        try {
            try {
                n.config.doc.execCommand("styleWithCSS", null, 1)
            } catch (r) {
                try {
                    n.config.doc.execCommand("useCSS", null, 0)
                } catch (i) {
                }
            }
            n.config.doc.execCommand(e, null, t)
        } catch (s) {
        }
    }, getInstance: function () {
        return this._inst || (this._inst = this.get("host").getInstance()), this._inst
    }, initializer: function () {
        e.mix(this.get("host"), {execCommand: function (e, t) {
            return this.exec.command(e, t)
        }, _execCommand: function (e, t) {
            return this.exec._command(e, t)
        }}), this.get("host").on("dom:keypress", e.bind(function (e) {
            this._lastKey = e.keyCode
        }, this))
    }, _wrapContent: function (e, t) {
        var n = this.getInstance().host.editorPara && !t ? !0 : !1;
        return n ? e = "<p>" + e + "</p>" : e += "<br>", e
    }}, {NAME: "execCommand", NS: "exec", ATTRS: {host: {value: !1}}, COMMANDS: {wrap: function (e, t) {
        var n = this.getInstance();
        return(new n.EditorSelection).wrapContent(t)
    }, inserthtml: function (t, n) {
        var r = this.getInstance();
        if (r.EditorSelection.hasCursor() || e.UA.ie)return(new r.EditorSelection).insertContent(n);
        this._command("inserthtml", n)
    }, insertandfocus: function (e, t) {
        var n = this.getInstance(), r, i;
        return n.EditorSelection.hasCursor() ? (t += n.EditorSelection.CURSOR, r = this.command("inserthtml", t), i = new n.EditorSelection, i.focusCursor(!0, !0)) : this.command("inserthtml", t), r
    }, insertbr: function () {
        var t = this.getInstance(), n = new t.EditorSelection, r = "<var>|</var>", i = null, s = e.UA.webkit ? "span.Apple-style-span,var" : "var", o = function (e) {
            var n = t.Node.create("<br>");
            return e.insert(n, "before"), n
        };
        n._selection.pasteHTML ? n._selection.pasteHTML(r) : this._command("inserthtml", r), t.all(s).each(function (t) {
            var n = !0, r;
            e.UA.webkit && (n = !1, t.get("innerHTML") === "|" && (n = !0)), n && (i = o(t), (!i.previous() || !i.previous().test("br")) && e.UA.gecko && (r = i.cloneNode(), i.insert(r, "after"), i = r), t.remove())
        }), e.UA.webkit && i && (o(i), n.selectNode(i))
    }, insertimage: function (e, t) {
        return this.command("inserthtml", '<img src="' + t + '">')
    }, addclass: function (e, t) {
        var n = this.getInstance();
        return(new n.EditorSelection).getSelected().addClass(t)
    }, removeclass: function (e, t) {
        var n = this.getInstance();
        return(new n.EditorSelection).getSelected().removeClass(t)
    }, forecolor: function (t, n) {
        var r = this.getInstance(), i = new r.EditorSelection, s;
        e.UA.ie || this._command("useCSS", !1);
        if (r.EditorSelection.hasCursor())return i.isCollapsed ? (i.anchorNode && i.anchorNode.get("innerHTML") === "&nbsp;" ? (i.anchorNode.setStyle("color", n), s = i.anchorNode) : (s = this.command("inserthtml", '<span style="color: ' + n + '">' + r.EditorSelection.CURSOR + "</span>"), i.focusCursor(!0, !0)), s) : this._command(t, n);
        this._command(t, n)
    }, backcolor: function (t, n) {
        var r = this.getInstance(), i = new r.EditorSelection, s;
        if (e.UA.gecko || e.UA.opera)t = "hilitecolor";
        e.UA.ie || this._command("useCSS", !1);
        if (r.EditorSelection.hasCursor())return i.isCollapsed ? (i.anchorNode && i.anchorNode.get("innerHTML") === "&nbsp;" ? (i.anchorNode.setStyle("backgroundColor", n), s = i.anchorNode) : (s = this.command("inserthtml", '<span style="background-color: ' + n + '">' + r.EditorSelection.CURSOR + "</span>"), i.focusCursor(!0, !0)), s) : this._command(t, n);
        this._command(t, n)
    }, hilitecolor: function () {
        return n.COMMANDS.backcolor.apply(this, arguments)
    }, fontname2: function (e, t) {
        this._command("fontname", t);
        var n = this.getInstance(), r = new n.EditorSelection;
        r.isCollapsed && this._lastKey !== 32 && r.anchorNode.test("font") && r.anchorNode.set("face", t)
    }, fontsize2: function (t, n) {
        this._command("fontsize", n);
        var r = this.getInstance(), i = new r.EditorSelection, s;
        i.isCollapsed && i.anchorNode && this._lastKey !== 32 && (e.UA.webkit && i.anchorNode.getStyle("lineHeight") && i.anchorNode.setStyle("lineHeight", ""), i.anchorNode.test("font") ? i.anchorNode.set("size", n) : e.UA.gecko && (s = i.anchorNode.ancestor(r.EditorSelection.DEFAULT_BLOCK_TAG), s && s.setStyle("fontSize", "")))
    }, insertunorderedlist: function () {
        this.command("list", "ul")
    }, insertorderedlist: function () {
        this.command("list", "ol")
    }, list: function (t, n) {
        var r = this.getInstance(), i, s = this, o = "dir", u = "yui3-touched", a, f, l, c, h, p, d, v, m, g, y = r.host.editorPara ? !0 : !1, b, w, E, S, x = new r.EditorSelection;
        t = "insert" + (n === "ul" ? "un" : "") + "orderedlist";
        if (e.UA.ie && !x.isCollapsed) {
            f = x._selection, i = f.htmlText, l = r.Node.create(i) || r.one("body");
            if (l.test("li") || l.one("li")) {
                this._command(t, null);
                return
            }
            l.test(n) ? (c = f.item ? f.item(0) : f.parentElement(), h = r.one(c), g = h.all("li"), p = "<div>", g.each(function (e) {
                p = s._wrapContent(e.get("innerHTML"))
            }), p += "</div>", d = r.Node.create(p), h.get("parentNode").test("div") && (h = h.get("parentNode")), h && h.hasAttribute(o) && (y ? d.all("p").setAttribute(o, h.getAttribute(o)) : d.setAttribute(o, h.getAttribute(o))), y ? h.replace(d.get("innerHTML")) : h.replace(d), f.moveToElementText && f.moveToElementText(d._node), f.select()) : (v = e.one(f.parentElement()), v.test(r.EditorSelection.BLOCKS) || (v = v.ancestor(r.EditorSelection.BLOCKS)), v && v.hasAttribute(o) && (a = v.getAttribute(o)), i.indexOf("<br>") > -1 ? i = i.split(/<br>/i) : (b = r.Node.create(i), ps = b ? b.all("p") : null, ps && ps.size() ? (i = [], ps.each(function (e) {
                i.push(e.get("innerHTML"))
            })) : i = [i]), m = "<" + n + ' id="ie-list">', e.each(i, function (e) {
                var t = r.Node.create(e);
                t && t.test("p") && (t.hasAttribute(o) && (a = t.getAttribute(o)), e = t.get("innerHTML")), m += "<li>" + e + "</li>"
            }), m += "</" + n + ">", f.pasteHTML(m), c = r.config.doc.getElementById("ie-list"), c.id = "", a && c.setAttribute(o, a), f.moveToElementText && f.moveToElementText(c)
                , f.select())
        } else e.UA.ie ? (v = r.one(x._selection.parentElement()), v.test("p") ? (v && v.hasAttribute(o) && (a = v.getAttribute(o)), i = e.EditorSelection.getText(v), i === "" ? (w = "", a && (w = ' dir="' + a + '"'), m = r.Node.create(e.Lang.sub("<{tag}{dir}><li></li></{tag}>", {tag: n, dir: w})), v.replace(m), x.selectNode(m.one("li"))) : this._command(t, null)) : this._command(t, null)) : (r.all(n).addClass(u), x.anchorNode.test(r.EditorSelection.BLOCKS) ? v = x.anchorNode : v = x.anchorNode.ancestor(r.EditorSelection.BLOCKS), v || (v = x.anchorNode.one(r.EditorSelection.BLOCKS)), v && v.hasAttribute(o) && (a = v.getAttribute(o)), v && v.test(n) ? (E = v.ancestor("p"), i = r.Node.create("<div/>"), c = v.all("li"), c.each(function (e) {
            i.append(s._wrapContent(e.get("innerHTML"), E))
        }), a && (y ? i.all("p").setAttribute(o, a) : i.setAttribute(o, a)), y && (i = r.Node.create(i.get("innerHTML"))), S = i.get("firstChild"), v.replace(i), x.selectNode(S)) : this._command(t, null), m = r.all(n), a && m.size() && m.each(function (e) {
            e.hasClass(u) || e.setAttribute(o, a)
        }), m.removeClass(u))
    }, justify: function (t, n) {
        if (e.UA.webkit) {
            var r = this.getInstance(), i = new r.EditorSelection, s = i.anchorNode, o, u = s.getStyle("backgroundColor");
            this._command(n), i = new r.EditorSelection, i.anchorNode.test("div") && (o = "<span>" + i.anchorNode.get("innerHTML") + "</span>", i.anchorNode.set("innerHTML", o), i.anchorNode.one("span").setStyle("backgroundColor", u), i.selectNode(i.anchorNode.one("span")))
        } else this._command(n)
    }, justifycenter: function () {
        this.command("justify", "justifycenter")
    }, justifyleft: function () {
        this.command("justify", "justifyleft")
    }, justifyright: function () {
        this.command("justify", "justifyright")
    }, justifyfull: function () {
        this.command("justify", "justifyfull")
    }}}), e.UA.ie && (n.COMMANDS.bold = function () {
        r.call(this, "bold", "b", "FONT-WEIGHT: bold")
    }, n.COMMANDS.italic = function () {
        r.call(this, "italic", "i", "FONT-STYLE: italic")
    }, n.COMMANDS.underline = function () {
        r.call(this, "underline", "u", "TEXT-DECORATION: underline")
    }), e.namespace("Plugin"), e.Plugin.ExecCommand = n
}, "true", {requires: ["frame"]});
