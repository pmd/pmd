// Detect small devices and move the TOC in line
function moveToc(){
    if(window.innerWidth < 1350){
        $( '#toc' ).detach().appendTo('#inline-toc').removeClass('position-fixed');
    } else {
        $( '#toc' ).detach().appendTo('.toc-col').addClass('position-fixed');
    }
}

$(document).ready(function () {
    // This handles the automatic toc. Use ## for subheads to auto-generate the on-page minitoc.
    // If you use html tags, you must supply an ID for the heading element in order for it to appear in the minitoc.
    $('#toc').toc({
        minimumHeaders: 0,
        listType: 'ul',
        showSpeed: 0,
        headers: 'h2,h3,h4',
    });

    // activate tooltips. although this is a bootstrap js function, it must be activated this way in your theme.
    $('[data-toggle="tooltip"]').tooltip({
        placement: 'top',
    });

    /**
     * AnchorJS
     */
    anchors.add('h2,h3,h4,h5');

    // Add an "Edit on GitHub" button to each header (except h1)
    let url = $('div.post-content').data('githubEditUrl');
    if ( url !== undefined ) {
        $('div.post-content')
            .find(':header:not(h1)')
            .append(
                '  <a class="edit-header" target="_blank" href=' +
                    url +
                    ' role="button">✏️️</a>'
            );
    }

    // Check if TOC needs to be moved on page load
    moveToc();

    // This highlights the active parent class in the navgoco sidebar. This is critical so that the parent expands
    // when you're viewing a page.
    // Note: the class needs to be added before navgoco is initialized. Navgoco uses then this information
    // to expand the menus.
    $( 'li.active' ).parents('li').toggleClass('active');

    // Initialize navgoco with default options
    $( '#mysidebar' ).navgoco({
        caretHtml: '',
        accordion: true,
        openClass: 'active',
        save: false,
        slide: {
            duration: 400,
            easing: 'swing'
        }
    });

    // Initialize jekyll search in topnav.
    SimpleJekyllSearch.init({
        searchInput: document.getElementById('search-input'),
        resultsContainer: document.getElementById('results-container'),
        json: 'search.json',
        searchResultTemplate: '<li><a href="{url}"><strong>{title}</strong><br>{summary}</a></li>',
        noResultsText: '<li>No results found.</li>',
        exclude: ['type', 'source'],
        limit: 20,
        fuzzy: false,
    });
    // Make sure to close and empty the search results after clicking one result item.
    // This is necessary, if we don't switch the page but only jump to a anchor on the
    // same page.
    $('#results-container').click(function() {
        $('#search-input').val('');
        $(this).empty();
    });
    // simple keyboard control of search results
    $('#search-input, body').on('keyup', function(e) {
        // arrow down: 40, arrow up: 38
        if (e.which !== 40 && e.which !== 38) {
            return;
        }
        if ($('#results-container li').length === 0) {
            return;
        }

        var current = $('#results-container li.selected');
        if (current.length === 0) {
            current = $('#results-container li')[0];
        } else {
            current = current[0];
            $(current).removeClass('selected');
            if (e.which === 40) {
                if (current.nextSibling !== null) {
                    current = current.nextSibling;
                }
            } else if (e.which === 38) {
                if (current.previousSibling !== null) {
                    current = current.previousSibling;
                }
            }
        }
        $(current).addClass('selected');
        $('a', current).focus();
        e.preventDefault();
        e.stopImmediatePropagation(); // avoid triggering another search and rerender the results
    });
    $('#results-container').on('mouseover', function(e) {
        $('#results-container li.selected').removeClass('selected');
        var selected = $(e.target).closest('li')[0];
        if (selected) {
            $(selected).addClass('selected');
            $('a', selected).focus();
        }
    });
    $('body').on('keyup', function(e) {
        // keyboard shortcut "s" for search
        if (e.which === 83) { // 83 = "s"
            $('#search-input').focus();
        }
        // keyboard shortcut "esc" for closing search result
        if (e.which === 27) { // 27 = "<esc>"
            $('#results-container').empty();
        }
    });
    $('body').on('click', function(e) {
        if ($('#results-container li').length > 0) {
            $('#results-container').empty();
        }
    });

    // Topnav toggle button for displaying/hiding nav sidebar
    $("#tg-sb-link").click(function(event) {
        $("#tg-sb-sidebar").toggle();
        $("#tg-sb-content").toggleClass('col-md-9');
        $("#tg-sb-content").toggleClass('col-md-12');
        $("#tg-sb-icon").toggleClass('fa-toggle-on');
        $("#tg-sb-icon").toggleClass('fa-toggle-off');
        event.preventDefault();
    });
});

// Check if TOC needs to be moved on window resizing
$(window).resize(function () {
    moveToc();
});
