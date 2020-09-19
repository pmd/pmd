$( document ).ready(function() {

    $('#mysidebar').height($(".nav").height());

    // this script says, if the height of the viewport is greater than 800px, then insert position-fixed class,
    // which makes the nav bar float in a fixed position as your scroll. If you have a lot of nav items,
    // this height may not work for you.
    var h = $(window).height();
    //console.log (h);
    if (h > 800) {
        $( "#mysidebar" ).attr("class", "nav position-fixed");
    }

    // activate tooltips. although this is a bootstrap js function, it must be activated this way in your theme.
    $('[data-toggle="tooltip"]').tooltip({
        placement : 'top'
    });

    /**
     * AnchorJS
     */
    anchors.add('h2,h3,h4,h5');

});
