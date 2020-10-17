/*
 * Adjusted sample from original: https://vestride.github.io/Shuffle/js/demos/homepage.js
 */
(function() {

'use strict';

var Shuffle = window.Shuffle;

var DocShuffle = function () {
    this.element = document.querySelector('#grid');

    this.shuffle = new Shuffle(this.element, {
      itemSelector: 'div[data-groups]',
      sizer: this.element.querySelector('.shuffle_sizer'),
    });

    this.addFilterButtons();
    this.addLinkTargetHighlighting();
};

DocShuffle.prototype.addFilterButtons = function () {
    var options = document.querySelector('.filter-options');

    if (!options) {
      return;
    }

    var filterButtons = Array.from(options.children);

    filterButtons.forEach(function (button) {
      button.addEventListener('click', this._handleFilterClick.bind(this), false);
    }, this);
};

DocShuffle.prototype._handleFilterClick = function (evt) {
    var btn = evt.currentTarget;
    var isActive = btn.classList.contains('active');
    var btnGroup = btn.getAttribute('data-group');

    this._removeActiveClassFromChildren(btn.parentNode);

    var filterGroup;
    if (isActive) {
        btn.classList.remove('active');
        filterGroup = Shuffle.ALL_ITEMS;
    } else {
        btn.classList.add('active');
        filterGroup = btnGroup;
    }

    this.shuffle.filter(filterGroup);
};

DocShuffle.prototype._removeActiveClassFromChildren = function (parent) {
    var children = parent.children;
    for (var i = children.length - 1; i >= 0; i--) {
      children[i].classList.remove('active');
    }
};

// Adds a pretty animation to links to shuffle panels
// Note: needs jquery-ui
DocShuffle.prototype.addLinkTargetHighlighting = function() {
    $("a[href^='#shuffle-panel']").click(function () {
        if (location.pathname.replace(/^\//, '') === this.pathname.replace(/^\//, '')
            && location.hostname === this.hostname) {
            var target = $(this.hash);

            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                $('#topbar-content-offset').animate({
                    scrollTop: $('#grid-rule').position().top
                }, {
                    duration: 500,
                    complete: function () {
                        target.effect("highlight", {}, 1000);
                    }
                });

                return false;
            }
        }
    });
};


document.addEventListener('DOMContentLoaded', function () {
    window.docShuffle = new DocShuffle();
});

})();
