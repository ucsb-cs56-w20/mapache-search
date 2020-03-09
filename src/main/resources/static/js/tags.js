// tags.js
var $document = $(document);

function tag_page_ready() {
	$filterBoxes = $(".filter-checkbox");

	function filterBy(e) {
		$this = $(this);
		if(this.checked) {
			var newURL = window.location.protocol + "//";
			newURL += window.location.host;
			newURL += "/tags/filter?filter=" + $this.val();
			window.location.href = newURL;
		}
	}

	$filterBoxes.on("change", filterBy);
}

$document.ready(tag_page_ready);