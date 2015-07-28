/*!
angular-xeditable - 0.1.8
Edit-in-place for angular.js
Build date: 2014-01-10 
*/
angular.module("xeditable", []).value("editableOptions", {
    theme: "default",
    buttons: "right",
    blurElem: "cancel",
    blurForm: "ignore",
    activate: "focus"
}), angular.module("xeditable").directive("editableBsdate", ["editableDirectiveFactory", function (a)
{
    return a(
    {
        directiveName: "editableBsdate",
        inputTpl: '<input type="text">'
    })}]), angular.module("xeditable").directive("editableBstime", ["editableDirectiveFactory", function (a)
{
    return a(
    {
        directiveName: "editableBstime",
        inputTpl: "<timepicker></timepicker>",
        render: function ()
        {
            this.parent.render.call(this);
            var a = angular.element('<div class="well well-small" style="display:inline-block;"></div>');
            a.attr("ng-model", this.inputEl.attr("ng-model")), this.inputEl.removeAttr("ng-model"), this.attrs.eNgChange && (a.attr("ng-change", this.inputEl.attr("ng-change")), this.inputEl.removeAttr("ng-change")), this.inputEl.wrap(a)
        }
    })}]), angular.module("xeditable").directive("editableCheckbox", ["editableDirectiveFactory", function (a)
{
    return a(
    {
        directiveName: "editableCheckbox",
        inputTpl: '<input type="checkbox">',
        render: function ()
        {
            this.parent.render.call(this), this.attrs.eTitle && (this.inputEl.wrap("<label></label>"), this.inputEl.after(angular.element("<span></span>").text(this.attrs.eTitle)))
        },
        autosubmit: function ()
        {
            var a = this;
            a.inputEl.bind("change", function ()
            {
                setTimeout(function ()
                {
                    a.scope.$apply(function ()
                    {
                        a.scope.$form.$submit()
                    })
                }, 500)
            })
        }
    })}]), angular.module("xeditable").directive("editableChecklist", ["editableDirectiveFactory", "editableNgOptionsParser", function (a, b)
{
    return a(
    {
        directiveName: "editableChecklist",
        inputTpl: "<span></span>",
        useCopy: !0,
        render: function ()
        {
            this.parent.render.call(this);
            var a = b(this.attrs.eNgOptions),
             c = '<label ng-repeat="' + a.ngRepeat + '">' + '<input type="checkbox" checklist-model="$parent.$data" checklist-value="' + a.locals.valueFn + '">' + '<span ng-bind="' + a.locals.displayFn + '"></span></label>';
            this.inputEl.removeAttr("ng-model"), this.inputEl.removeAttr("ng-options"), this.inputEl.html(c)
        }
    })}]), function ()
{
    var a = "text|email|tel|number|url|search|color|date|datetime|time|month|week".split("|");
    angular.forEach(a, function (a)
    {
        var b = "editable" + a.charAt(0).toUpperCase() + a.slice(1);
        angular.module("xeditable").directive(b, ["editableDirectiveFactory", function (c)
        {
            return c(
            {
                directiveName: b,
                inputTpl: '<input type="' + a + '">'
            })}])
    }), angular.module("xeditable").directive("editableRange", ["editableDirectiveFactory", function (a)
    {
        return a(
        {
            directiveName: "editableRange",
            inputTpl: '<input type="range" id="range" name="range">',
            render: function ()
            {
                this.parent.render.call(this), this.inputEl.after("<output>{{$data}}</output>")
            }
        })}])
}(), angular.module("xeditable").directive("editableRadiolist", ["editableDirectiveFactory", "editableNgOptionsParser", function (a, b)
{
    return a(
    {
        directiveName: "editableRadiolist",
        inputTpl: "<span></span>",
        render: function ()
        {
            this.parent.render.call(this);
            var a = b(this.attrs.eNgOptions),
             c = '<label ng-repeat="' + a.ngRepeat + '">' + '<input type="radio" ng-model="$parent.$data" value="{{' + a.locals.valueFn + '}}">' + '<span ng-bind="' + a.locals.displayFn + '"></span></label>';
            this.inputEl.removeAttr("ng-model"), this.inputEl.removeAttr("ng-options"), this.inputEl.html(c)
        },
        autosubmit: function ()
        {
            var a = this;
            a.inputEl.bind("change", function ()
            {
                setTimeout(function ()
                {
                    a.scope.$apply(function ()
                    {
                        a.scope.$form.$submit()
                    })
                }, 500)
            })
        }
    })}]), angular.module("xeditable").directive("editableSelect", ["editableDirectiveFactory", function (a)
{
    return a(
    {
        directiveName: "editableSelect",
        inputTpl: "<select></select>",
        autosubmit: function ()
        {
            var a = this;
            a.inputEl.bind("change", function ()
            {
                a.scope.$apply(function ()
                {
                    a.scope.$form.$submit()
                })
            })
        }
    })}]), angular.module("xeditable").directive("editableTextarea", ["editableDirectiveFactory", function (a)
{
    return a(
    {
        directiveName: "editableTextarea",
        inputTpl: "<textarea></textarea>",
        addListeners: function ()
        {
            var a = this;
            a.parent.addListeners.call(a), a.single && "no" !== a.buttons && a.autosubmit()
        },
        autosubmit: function ()
        {
            var a = this;
            a.inputEl.bind("keydown", function (b)
            {
                (b.ctrlKey || b.metaKey) && 13 === b.keyCode && a.scope.$apply(function ()
                {
                    a.scope.$form.$submit()
                })
            })
        }
    })}]), angular.module("xeditable").factory("editableController", ["$q", "editableUtils", function (a, b)
{
    function c(a, c, d, e, f, g, h, i, j)
    {
        var k, l, m = this;
        m.scope = a, m.elem = d, m.attrs = c, m.inputEl = null, m.editorEl = null, m.single = !0, m.error = "", m.theme = f[g.theme] || f["default"], m.parent =
        {
        }, m.inputTpl = "", m.directiveName = "", m.useCopy = !1, m.single = null, m.buttons = "right", m.init = function (b)
        {
            if (m.single = b, m.name = c.eName || c[m.directiveName], !c[m.directiveName]) throw "You should provide value for `" + m.directiveName + "` in editable element!";
            k = e(c[m.directiveName]), m.buttons = m.single ? m.attrs.buttons || g.buttons : "no", c.eName && m.scope.$watch("$data", function (a)
            {
                m.scope.$form.$data[c.eName] = a
            }), c.onshow && (m.onshow = function ()
            {
                return m.catchError(e(c.onshow)(a))
            }), c.onhide && (m.onhide = function ()
            {
                return e(c.onhide)(a)
            }), c.oncancel && (m.oncancel = function ()
            {
                return e(c.oncancel)(a)
            }), c.onbeforesave && (m.onbeforesave = function ()
            {
                return m.catchError(e(c.onbeforesave)(a))
            }), c.onaftersave && (m.onaftersave = function ()
            {
                return m.catchError(e(c.onaftersave)(a))
            }), a.$parent.$watch(c[m.directiveName], function ()
            {
                m.handleEmpty()
            })
        }, m.render = function ()
        {
            var a = m.theme;
            m.inputEl = angular.element(m.inputTpl), m.controlsEl = angular.element(a.controlsTpl), m.controlsEl.append(m.inputEl), "no" !== m.buttons && (m.buttonsEl = angular.element(a.buttonsTpl), m.submitEl = angular.element(a.submitTpl), m.cancelEl = angular.element(a.cancelTpl), m.buttonsEl.append(m.submitEl).append(m.cancelEl), m.controlsEl.append(m.buttonsEl), m.inputEl.addClass("editable-has-buttons")), m.errorEl = angular.element(a.errorTpl), m.controlsEl.append(m.errorEl), m.editorEl = angular.element(m.single ? a.formTpl : a.noformTpl), m.editorEl.append(m.controlsEl);
            for (var d in c.$attr) if (!(d.length <= 1))
            {
                var e = !1,
                 f = d.substring(1, 2);
                if ("e" === d.substring(0, 1) && f === f.toUpperCase() && (e = d.substring(1), "Form" !== e && "NgSubmit" !== e))
                {
                    e = e.substring(0, 1).toLowerCase() + b.camelToDash(e.substring(1));
                    var h = "" === c[d] ? e : c[d];
                    m.inputEl.attr(e, h)
                }
            }
            m.inputEl.addClass("editable-input"), m.inputEl.attr("ng-model", "$data"), m.editorEl.addClass(b.camelToDash(m.directiveName)), m.single && (m.editorEl.attr("editable-form", "$form"), m.editorEl.attr("blur", m.attrs.blur || ("no" === m.buttons ? "cancel" : g.blurElem))), angular.isFunction(a.postrender) && a.postrender.call(m)
        }, m.setLocalValue = function ()
        {
            m.scope.$data = m.useCopy ? angular.copy(k(a.$parent)) : k(a.$parent)
        }, m.show = function ()
        {
            return m.setLocalValue(), m.render(), d.after(m.editorEl), i(m.editorEl)(a), m.addListeners(), d.addClass("editable-hide"), m.onshow()
        }, m.hide = function ()
        {
            return m.editorEl.remove(), d.removeClass("editable-hide"), m.onhide()
        }, m.cancel = function ()
        {
            m.oncancel()
        }, m.addListeners = function ()
        {
            m.inputEl.bind("keyup", function (a)
            {
                if (m.single) switch (a.keyCode)
                {
                case 27:
                    m.scope.$apply(function ()
                    {
                        m.scope.$form.$cancel()
                    })
                }
            }), m.single && "no" === m.buttons && m.autosubmit(), m.editorEl.bind("click", function (a)
            {
                1 === a.which && m.scope.$form.$visible && (m.scope.$form._clicked = !0)
            })
        }, m.setWaiting = function (a)
        {
            a ? (l = !m.inputEl.attr("disabled") && !m.inputEl.attr("ng-disabled") && !m.inputEl.attr("ng-enabled"), l && (m.inputEl.attr("disabled", "disabled"), m.buttonsEl && m.buttonsEl.find("button").attr("disabled", "disabled"))) : l && (m.inputEl.removeAttr("disabled"), m.buttonsEl && m.buttonsEl.find("button").removeAttr("disabled"))
        }, m.activate = function ()
        {
            setTimeout(function ()
            {
                var a = m.inputEl[0];
                "focus" === g.activate && a.focus && a.focus(), "select" === g.activate && a.select && a.select()
            }, 0)
        }, m.setError = function (b)
        {
            angular.isObject(b) || (a.$error = b, m.error = b)
        }, m.catchError = function (a, b)
        {
            return angular.isObject(a) && b !== !0 ? j.when(a).then(angular.bind(this, function (a)
            {
                this.catchError(a, !0)
            }), angular.bind(this, function (a)
            {
                this.catchError(a, !0)
            })) : b && angular.isObject(a) && a.status && 200 !== a.status && a.data && angular.isString(a.data) ? (this.setError(a.data), a = a.data) : angular.isString(a) && this.setError(a), a
        }, m.save = function ()
        {
            k.assign(a.$parent, angular.copy(m.scope.$data))
        }, m.handleEmpty = function ()
        {
            var b = k(a.$parent),
             c = null === b || void 0 === b || "" === b || angular.isArray(b) && 0 === b.length;
            d.toggleClass("editable-empty", c)
        }, m.autosubmit = angular.noop, m.onshow = angular.noop, m.onhide = angular.noop, m.oncancel = angular.noop, m.onbeforesave = angular.noop, m.onaftersave = angular.noop
    }
    return c.$inject = ["$scope", "$attrs", "$element", "$parse", "editableThemes", "editableOptions", "$rootScope", "$compile", "$q"], c}]), angular.module("xeditable").factory("editableDirectiveFactory", ["$parse", "$compile", "editableThemes", "$rootScope", "$document", "editableController", "editableFormController", function (a, b, c, d, e, f, g)
{
    return function (b)
    {
        return {
            restrict: "A",
            scope: !0,
            require: [b.directiveName, "?^form"],
            controller: f,
            link: function (c, f, h, i)
            {
                var j, k = i[0],
                 l = !1;
                if (i[1]) j = i[1], l = !0;
                else if (h.eForm)
                {
                    var m = a(h.eForm)(c);
                    if (m) j = m, l = !0;
                    else for (var n = 0; n < e[0].forms.length; n++) if (e[0].forms[n].name === h.eForm)
                    {
                        j = null, l = !0;
                        break
                    }
                }
                if (angular.forEach(b, function (a, b)
                {
                    void 0 !== k[b] && (k.parent[b] = k[b])
                }), angular.extend(k, b), k.init(!l), c.$editable = k, f.addClass("editable"), l) if (j)
                {
                    if (c.$form = j, !c.$form.$addEditable) throw "Form with editable elements should have `editable-form` attribute.";
                    c.$form.$addEditable(k)
                }
                else d.$$editableBuffer = d.$$editableBuffer || {
                }, d.$$editableBuffer[h.eForm] = d.$$editableBuffer[h.eForm] || [], d.$$editableBuffer[h.eForm].push(k), c.$form = null;
                else c.$form = g(), c.$form.$addEditable(k), h.eForm && (c.$parent[h.eForm] = c.$form), h.eForm || (f.addClass("editable-click"), f.bind("click", function (a)
                {
                    a.preventDefault(), a.editable = k, c.$apply(function ()
                    {
                        c.$form.$show()
                    })
                }))
            }
        }
    }}]), angular.module("xeditable").factory("editableFormController", ["$parse", "$document", "$rootScope", "editablePromiseCollection", "editableUtils", function (a, b, c, d, e)
{
    var f = [];
    b.bind("click", function (a)
    {
        if (1 === a.which)
        {
            for (var b = [], d = [], e = 0; e < f.length; e++) f[e]._clicked ? f[e]._clicked = !1 : f[e].$waiting || ("cancel" === f[e]._blur && b.push(f[e]), "submit" === f[e]._blur && d.push(f[e]));
            (b.length || d.length) && c.$apply(function ()
            {
                angular.forEach(b, function (a)
                {
                    a.$cancel()
                }), angular.forEach(d, function (a)
                {
                    a.$submit()
                })
            })
        }
    });
    var g =
    {
        $addEditable: function (a)
        {
            this.$editables.push(a), a.elem.bind("$destroy", angular.bind(this, this.$removeEditable, a)), a.scope.$form || (a.scope.$form = this), this.$visible && a.catchError(a.show())
        },
        $removeEditable: function (a)
        {
            for (var b = 0; b < this.$editables.length; b++) if (this.$editables[b] === a) return this.$editables.splice(b, 1), void 0
        },
        $show: function ()
        {
            if (!this.$visible)
            {
                this.$visible = !0;
                var a = d();
                a.when(this.$onshow()), this.$setError(null, ""), angular.forEach(this.$editables, function (b)
                {
                    a.when(b.show())
                }), a.then(
                {
                    onWait: angular.bind(this, this.$setWaiting),
                    onTrue: angular.bind(this, this.$activate),
                    onFalse: angular.bind(this, this.$activate),
                    onString: angular.bind(this, this.$activate)
                }), setTimeout(angular.bind(this, function ()
                {
                    this._clicked = !1, -1 === e.indexOf(f, this) && f.push(this)
                }), 0)
            }
        },
        $activate: function (a)
        {
            var b;
            if (this.$editables.length)
            {
                if (angular.isString(a)) for (b = 0; b < this.$editables.length; b++) if (this.$editables[b].name === a) return this.$editables[b].activate(), void 0;
                for (b = 0; b < this.$editables.length; b++) if (this.$editables[b].error) return this.$editables[b].activate(), void 0;
                this.$editables[0].activate()
            }
        },
        $hide: function ()
        {
            this.$visible && (this.$visible = !1, this.$onhide(), angular.forEach(this.$editables, function (a)
            {
                a.hide()
            }), e.arrayRemove(f, this))
        },
        $cancel: function ()
        {
            this.$visible && (this.$oncancel(), angular.forEach(this.$editables, function (a)
            {
                a.cancel()
            }), this.$hide())
        },
        $setWaiting: function (a)
        {
            this.$waiting = !! a, angular.forEach(this.$editables, function (b)
            {
                b.setWaiting( !! a)
            })
        },
        $setError: function (a, b)
        {
            angular.forEach(this.$editables, function (c)
            {
                a && c.name !== a || c.setError(b)
            })
        },
        $submit: function ()
        {
            function a(a)
            {
                var b = d();
                b.when(this.$onbeforesave()), b.then(
                {
                    onWait: angular.bind(this, this.$setWaiting),
                    onTrue: a ? angular.bind(this, this.$save) : angular.bind(this, this.$hide),
                    onFalse: angular.bind(this, this.$hide),
                    onString: angular.bind(this, this.$activate)
                })
            }
            if (!this.$waiting)
            {
                this.$setError(null, "");
                var b = d();
                angular.forEach(this.$editables, function (a)
                {
                    b.when(a.onbeforesave())
                }), b.then(
                {
                    onWait: angular.bind(this, this.$setWaiting),
                    onTrue: angular.bind(this, a, !0),
                    onFalse: angular.bind(this, a, !1),
                    onString: angular.bind(this, this.$activate)
                })
            }
        },
        $save: function ()
        {
            angular.forEach(this.$editables, function (a)
            {
                a.save()
            });
            var a = d();
            a.when(this.$onaftersave()), angular.forEach(this.$editables, function (b)
            {
                a.when(b.onaftersave())
            }), a.then(
            {
                onWait: angular.bind(this, this.$setWaiting),
                onTrue: angular.bind(this, this.$hide),
                onFalse: angular.bind(this, this.$hide),
                onString: angular.bind(this, this.$activate)
            })
        },
        $onshow: angular.noop,
        $oncancel: angular.noop,
        $onhide: angular.noop,
        $onbeforesave: angular.noop,
        $onaftersave: angular.noop
    };
    return function ()
    {
        return angular.extend(
        {
            $editables: [],
            $visible: !1,
            $waiting: !1,
            $data: {
            },
            _clicked: !1,
            _blur: null
        }, g)
    }}]), angular.module("xeditable").directive("editableForm", ["$rootScope", "$parse", "editableFormController", "editableOptions", function (a, b, c, d)
{
    return {
        restrict: "A",
        require: ["form"],
        compile: function ()
        {
            return {
                pre: function (b, d, e, f)
                {
                    var g, h = f[0];
                    e.editableForm ? b[e.editableForm] && b[e.editableForm].$show ? (g = b[e.editableForm], angular.extend(h, g)) : (g = c(), b[e.editableForm] = g, angular.extend(g, h)) : (g = c(), angular.extend(h, g));
                    var i = a.$$editableBuffer,
                     j = h.$name;
                    j && i && i[j] && (angular.forEach(i[j], function (a)
                    {
                        g.$addEditable(a)
                    }), delete i[j])
                },
                post: function (a, c, e, f)
                {
                    var g;
                    g = e.editableForm && a[e.editableForm] && a[e.editableForm].$show ? a[e.editableForm] : f[0], e.onshow && (g.$onshow = angular.bind(g, b(e.onshow), a)), e.onhide && (g.$onhide = angular.bind(g, b(e.onhide), a)), e.oncancel && (g.$oncancel = angular.bind(g, b(e.oncancel), a)), e.shown && b(e.shown)(a) && g.$show(), g._blur = e.blur || d.blurForm, e.ngSubmit || e.submit || (e.onbeforesave && (g.$onbeforesave = function ()
                    {
                        return b(e.onbeforesave)(a, {
                            $data: g.$data
                        })
                    }), e.onaftersave && (g.$onaftersave = function ()
                    {
                        return b(e.onaftersave)(a, {
                            $data: g.$data
                        })
                    }), c.bind("submit", function (b)
                    {
                        b.preventDefault(), a.$apply(function ()
                        {
                            g.$submit()
                        })
                    })), c.bind("click", function (a)
                    {
                        1 === a.which && g.$visible && (g._clicked = !0)
                    })
                }
            }
        }
    }}]), angular.module("xeditable").factory("editablePromiseCollection", ["$q", function (a)
{
    function b()
    {
        return {
            promises: [],
            hasFalse: !1,
            hasString: !1,
            when: function (b, c)
            {
                if (b === !1) this.hasFalse = !0;
                else if (!c && angular.isObject(b)) this.promises.push(a.when(b));
                else
                {
                    if (!angular.isString(b)) return;
                    this.hasString = !0
                }
            },
            then: function (b)
            {
                function c()
                {
                    h.hasString || h.hasFalse ? !h.hasString && h.hasFalse ? e() : f() : d()
                }
                b = b || {
                };
                var d = b.onTrue || angular.noop,
                 e = b.onFalse || angular.noop,
                 f = b.onString || angular.noop,
                 g = b.onWait || angular.noop,
                 h = this;
                this.promises.length ? (g(!0), a.all(this.promises).then(function (a)
                {
                    g(!1), angular.forEach(a, function (a)
                    {
                        h.when(a, !0)
                    }), c()
                }, function ()
                {
                    g(!1), f()
                })) : c()
            }
        }
    }
    return b}]), angular.module("xeditable").factory("editableUtils", [function ()
{
    return {
        indexOf: function (a, b)
        {
            if (a.indexOf) return a.indexOf(b);
            for (var c = 0; c < a.length; c++) if (b === a[c]) return c;
            return -1
        },
        arrayRemove: function (a, b)
        {
            var c = this.indexOf(a, b);
            return c >= 0 && a.splice(c, 1), b
        },
        camelToDash: function (a)
        {
            var b = /[A-Z]/g;
            return a.replace(b, function (a, b)
            {
                return (b ? "-" : "") + a.toLowerCase()
            })
        },
        dashToCamel: function (a)
        {
            var b = /([\:\-\_]+(.))/g,
             c = /^moz([A-Z])/;
            return a.replace(b, function (a, b, c, d)
            {
                return d ? c.toUpperCase() : c
            }).replace(c, "Moz$1")
        }
    }}]), angular.module("xeditable").factory("editableNgOptionsParser", [function ()
{
    function a(a)
    {
        var c;
        if (!(c = a.match(b))) throw "ng-options parse error";
        var d, e = c[2] || c[1],
         f = c[4] || c[6],
         g = c[5],
         h = (c[3] || "", c[2] ? c[1] : f),
         i = c[7],
         j = c[8],
         k = j ? c[8] : null;
        return void 0 === g ? (d = f + " in " + i, void 0 !== j && (d += " track by " + k)) : d = "(" + g + ", " + f + ") in " + i, {
            ngRepeat: d,
            locals: {
                valueName: f,
                keyName: g,
                valueFn: h,
                displayFn: e
            }
        }
    }
    var b = /^\s*(.*?)(?:\s+as\s+(.*?))?(?:\s+group\s+by\s+(.*))?\s+for\s+(?:([\$\w][\$\w]*)|(?:\(\s*([\$\w][\$\w]*)\s*,\s*([\$\w][\$\w]*)\s*\)))\s+in\s+(.*?)(?:\s+track\s+by\s+(.*?))?$/;
    return a}]), angular.module("xeditable").factory("editableThemes", function ()
{
    var a =
    {
        "default": {
            formTpl: '<form class="editable-wrap"></form>',
            noformTpl: '<span class="editable-wrap"></span>',
            controlsTpl: '<span class="editable-controls"></span>',
            inputTpl: "",
            errorTpl: '<div class="editable-error" ng-show="$error" ng-bind="$error"></div>',
            buttonsTpl: '<span class="editable-buttons"></span>',
            submitTpl: '<button type="submit">save</button>',
            cancelTpl: '<button type="button" ng-click="$form.$cancel()">cancel</button>'
        },
        bs2: {
            formTpl: '<form class="form-inline editable-wrap" role="form"></form>',
            noformTpl: '<span class="editable-wrap"></span>',
            controlsTpl: '<div class="editable-controls controls control-group" ng-class="{\'error\': $error}"></div>',
            inputTpl: "",
            errorTpl: '<div class="editable-error help-block" ng-show="$error" ng-bind="$error"></div>',
            buttonsTpl: '<span class="editable-buttons"></span>',
            submitTpl: '<button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span></button>',
            cancelTpl: '<button type="button" class="btn" ng-click="$form.$cancel()"><span class="icon-remove"></span></button>'
        },
        bs3: {
            formTpl: '<form class="form-inline editable-wrap" role="form"></form>',
            noformTpl: '<span class="editable-wrap"></span>',
            controlsTpl: '<div class="editable-controls form-group" ng-class="{\'has-error\': $error}"></div>',
            inputTpl: "",
            errorTpl: '<div class="editable-error help-block" ng-show="$error" ng-bind="$error"></div>',
            buttonsTpl: '<span class="editable-buttons"></span>',
            submitTpl: '<button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-ok"></span></button>',
            cancelTpl: '<button type="button" class="btn btn-default" ng-click="$form.$cancel()"><span class="glyphicon glyphicon-remove"></span></button>',
            buttonsClass: "",
            inputClass: "",
            postrender: function ()
            {
                switch (this.directiveName)
                {
                case "editableText":
                case "editableSelect":
                case "editableTextarea":
                case "editableEmail":
                case "editableTel":
                case "editableNumber":
                case "editableUrl":
                case "editableSearch":
                case "editableDate":
                case "editableDatetime":
                case "editableTime":
                case "editableMonth":
                case "editableWeek":
                    if (this.inputEl.addClass("form-control"), this.theme.inputClass)
                    {
                        if (this.inputEl.attr("multiple") && ("input-sm" === this.theme.inputClass || "input-lg" === this.theme.inputClass)) break;
                        this.inputEl.addClass(this.theme.inputClass)
                    }
                }
                this.buttonsEl && this.theme.buttonsClass && this.buttonsEl.find("button").addClass(this.theme.buttonsClass)
            }
        }
    };
    return a
});