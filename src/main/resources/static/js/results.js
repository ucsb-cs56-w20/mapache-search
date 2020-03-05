function onLoad() {
	var tagButtons = document.getElementsByClassName( "tag-button-text" ),
		i;

	function showTagDropdown(e) {
		console.log( e );
	}

	for( i = 0; i < tagButtons.length; i++ ) {
		tagButtons[ i ].addEventListener( "mousedown", showTagDropdown );
	}
}

window.addEventListener("load", onLoad);