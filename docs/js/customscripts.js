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

    $('#mysidebar').height($(".nav").height());

    // this script says, if the height of the viewport is greater than 600px, then insert position-fixed class,
    // which makes the nav bar float in a fixed position as your scroll. If you have a lot of nav items,
    // this height may not work for you.
    var h = $(window).height();
    //console.log (h);
    if (h > 600) {
        $( '#mysidebar' ).attr('class', 'nav position-fixed');
    }

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
});

// Check if TOC needs to be moved on window resizing
$(window).resize(function () {
    moveToc();
});
