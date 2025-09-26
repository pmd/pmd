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
        noBackToTopLinks: true,
    });

    /**
     * AnchorJS
     */
    anchors.options.icon='';
    anchors.options.class='fas fa-link fa-xs';
    anchors.add('h2,h3,h4,h5');

    // Add an "Edit on GitHub" button to each header (except h1)
    let url = $('div.post-content').data('githubEditUrl');
    if ( url !== undefined ) {
        $('div.post-content')
            .find(':header:not(h1)')
            .append(
                '  <a class="edit-header" target="_blank" href=' +
                    url +
                    ' role="button" data-toggle="tooltip" data-placement="top" title="Edit on GitHub"><i class="fas fa-edit fa-xs"></i>️</a>'
            );
    }

    // Add an "copy url" button to each header
    document.querySelectorAll('.anchorjs-link')
            .forEach(e => {
                const template = document.createElement('template');
                template.innerHTML = '<a class="copy-anchor-url" href="#" data-toggle="tooltip" data-placement="top" title="Copy URL"><i class="fas fa-copy fa-xs"></i></a>';
                e.parentNode.append(template.content.firstChild);
            });
    document.addEventListener('click', event => {
        let target = null;
        if (event.target.classList.contains('copy-anchor-url')) {
            target = event.target;
        } else if (event.target.parentNode.classList.contains('copy-anchor-url')) {
            target = event.target.parentNode;
        }

        if (target) {
            if (navigator.clipboard) {
                const url = new URL(location.href);
                url.hash = event.target.parentNode.id;
                event.preventDefault();
                event.stopImmediatePropagation();

                navigator.clipboard.writeText(url)
                         .then(() => {
                            target.firstChild.classList.remove('fa-copy');
                            target.firstChild.classList.add('fa-check');
                            window.setTimeout(() => {
                                target.firstChild.classList.remove('fa-check');
                                target.firstChild.classList.add('fa-copy');
                            }, 500);
                         });
            }
        }
    });

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
        searchResultTemplate: '<li><a href="{url}">{title}</a></li>',
        noResultsText: '{{site.data.strings.search_no_results_text}}',
        limit: 10,
        fuzzy: true,
    });
    // Make sure to close and empty the search results after clicking one result item.
    // This is necessary, if we don't switch the page but only jump to a anchor on the
    // same page.
    $('#results-container').click(function() {
        $('#search-input').val('');
        $(this).empty();
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

    // activate tooltips. although this is a bootstrap js function, it must be activated this way in your theme.
    $('[data-toggle="tooltip"]').tooltip({
        placement: 'top',
    });
});

// Check if TOC needs to be moved on window resizing
$(window).resize(function () {
    moveToc();
});
