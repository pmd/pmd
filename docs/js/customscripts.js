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
        limit: 20,
        fuzzy: false,
    });
    // Make sure to close and empty the search results after clicking one result item.
    // This is necessary, if we don't switch the page but only jump to a anchor on the
    // same page.
    document.getElementById('results-container').addEventListener('click', e => {
        document.getElementById('search-input').value = '';
        e.target.innerHTML = '';
    });
    // simple keyboard control of search results
    document.querySelectorAll('#search-input, body').forEach(element => {
        element.addEventListener('keyup', e => {
            if (e.key !== 'ArrowDown' && e.key !== 'ArrowUp') {
                return;
            }
            if (document.querySelectorAll('#results-container li').length === 0) {
                return;
            }

            let current = document.querySelector('#results-container li.selected');
            if (!current) {
                current = document.querySelector('#results-container li');
            } else {
                current.classList.remove('selected');
                if (e.key === 'ArrowDown') {
                    if (current.nextSibling != null) {
                        current = current.nextSibling;
                    }
                } else if (e.key === 'ArrowUp') {
                    if (current.previousSibling !== null) {
                        current = current.previousSibling;
                    }
                }
            }
            current.classList.add('selected');
            current.querySelector('a').focus();
            e.preventDefault();
            e.stopImmediatePropagation(); // avoid triggering another search and rerender the results
        });
    });
    document.getElementById('results-container').addEventListener('mouseover', e => {
        let selected = document.getElementById('results-container').querySelector('li.selected')
        if (selected) {
            selected.classList.remove('selected');
        }
        let newSelected = e.target.closest('li');
        if (newSelected) {
            newSelected.classList.add('selected');
            if (document.activeElement !== document.getElementById('search-input')) {
                newSelected.querySelector('a').focus();
            }
        }
    });
    document.body.addEventListener('keyup', e => {
        if (e.key === 's') {
            document.getElementById('search-input').focus();
        }
        if (e.key === 'Escape') {
            document.getElementById('results-container').innerHTML = '';
        }
    });
    document.body.addEventListener('click', e => {
        const resultsContainer = document.getElementById('results-container');
        if (resultsContainer.querySelectorAll('li').length > 0) {
            resultsContainer.innerHTML = '';
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
