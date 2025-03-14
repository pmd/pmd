// needed for nav tabs on pages. See Formatting > Nav tabs for more details.
// script from http://stackoverflow.com/questions/10523433/how-do-i-keep-the-current-tab-active-with-twitter-bootstrap-after-a-page-reload
(function() {
    var registerTabEvent = function() {
        $('a[data-toggle="pill"], a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
            var tabId, json, parentId, tabsState;

            tabsState = localStorage.getItem('tabs-state');
            json = JSON.parse(tabsState || "{}");
            parentId = $(e.target).parents('ul.nav.nav-pills, ul.nav.nav-tabs').attr('id');
            tabId = $(e.target).attr('id');
            json[parentId] = tabId;

            return localStorage.setItem('tabs-state', JSON.stringify(json));
        });
    };

    document.addEventListener('DOMContentLoaded', function () {
        var json, tabsState;
        tabsState = localStorage.getItem('tabs-state');
        json = JSON.parse(tabsState || '{}');

        $.each(json, function(containerId, tabId) {
            if (containerId && containerId !== 'undefined' && tabId && tabId !== 'undefined'
                && tabId.indexOf('#') !== 0) {
                $('#' + tabId).tab('show');
            }
        });

        $('ul.nav.nav-pills, ul.nav.nav-tabs').each(function() {
            var $this = $(this);
            if (!json[$this.attr('id')]) {
                return $this.find('a[data-toggle=tab]:first, a[data-toggle=pill]:first').tab('show');
            }
        });

        // cleanup "undefined" entries
        delete json['undefined'];
        localStorage.setItem('tabs-state', JSON.stringify(json));

        registerTabEvent();
    });
})();
