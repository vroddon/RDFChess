jQuery(function($) {
  function loadChessGame(container, options, callback) {
    var chess = $('.board', container).chess(options);
    $('.back', container).click(function() {
      chess.transitionBackward();
      $('.annot', container).text( chess.annotation() );
      return false;
    });

    $('.next', container).click(function() {
      chess.transitionForward();
      $('.annot', container).text( chess.annotation() );
      return false;
    });

    $('.flip', container).click(function() {
      chess.flipBoard();
      return false;
    });

    if ( typeof callback != "undefined" ) { callback(chess) };
  }
    loadChessGame( '#game3', { pgn : $('#pgn-fischer-spassky').html() } );
});
