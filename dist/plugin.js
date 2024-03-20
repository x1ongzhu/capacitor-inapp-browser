var capacitorInAppBrowser = (function (exports, core) {
    'use strict';

    const InAppBrowser = core.registerPlugin('InAppBrowser', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.InAppBrowserWeb()),
    });

    class InAppBrowserWeb extends core.WebPlugin {
        async open(options) {
            // TODO:
        }
        async close() {
            // TODO:
        }
        async postMessage(options) {
            // TODO:
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        InAppBrowserWeb: InAppBrowserWeb
    });

    exports.InAppBrowser = InAppBrowser;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
