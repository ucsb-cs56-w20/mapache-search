$(document).ready( function() {
    var $tagButtons = $(".tag-button-text"),
        $tagItems = $(".tag-menu-item"),
        $voteButtons = $(".vb"),
        $filterInput = $(".js-tag-filter-field"),
        $document = $(document),
        $currentDropdown;

    function showTagDropdown(e) {
        var $this = $(this);
        var buttonId = $this.attr("id");
        var dropdownId = buttonId.replace("button", "dropdown");
        if ($currentDropdown) $currentDropdown.hide();
        $currentDropdown = $("#" + dropdownId);
        $currentDropdown.show();
    }

    function hideTagDropdown(e) {
        var $target = $(e.target);
        if (!$target.closest(".tag-dropdown").length &&
            !$target.closest(".tag-button-text").length) {
            $currentDropdown.hide();
        }
    }

    function toggleTag(e) {
        if (e.button) return;

        var $this = $(this);
        var $tagName = $this.find(".tag-menu-item-name");
        var $result = $this.closest(".searchresult-row");
        var $vote = $result.find(".vote-info");

        var tagName = $tagName[0].innerHTML;
        var resultId = $vote.attr("id");
        
        $.ajax({
            url: "../updateTags",
            method: "GET",
            data: {
                tagName: tagName,
                id: resultId,
            }
        }).done(function( html ) {
            console.log(html);
        });

        var $check = $this.find(".tag-check");
        $check.toggle();
    }

    function filterTags(e) {
        var $this = $(this);
        var $dropdown = $this.closest(".tag-dropdown");
        var $items = $dropdown.find(".tag-menu-item");

        var query = $this.val().toLowerCase().trim();
        var $item, itemName, i;
        for (i = 0; i < $items.length; i++) {
            $item = $($items[i]);
            itemName = $item.find('.tag-menu-item-name').text().toLowerCase().trim();
            if (query != itemName.substring(0, query.length)) {
                $item.hide();
            } else {
                $item.show();
            }
        }
    }

    function updateVote(e) {
        var direction = ""; // parameter which tells the API whether this was upvote or downvote
        var offset = 0; // how much the vote count should change by
        var otherElement = "";
        if ($(this).hasClass("vb-up")) {
            direction = "up";
            offset = 1;
            otherElement = ".vb-down";
        } else {
            direction = "down";
            offset = -1;
            otherElement = ".vb-up";
        }
        if ($(this).hasClass("vb-clicked")) {
            direction = "none"; // undo the vote that's already been made
            offset *= -1; // reverse directions; we want to undo what we did before
        } else {
            var el = $(this).parent().children(otherElement);
            if (el.hasClass("vb-clicked")) {
                offset *= 2; // double the amount of change; we are now going from -1 to +1 or +1 to -1, which is a diff of 2
                el.removeClass("vb-clicked");
            }
        }
        var query = $(this).parent().data("url");
        // hit the API endpoint to change the vote in the backend
        $.ajax({
            url: "../updateVote",
            method: "GET",
            data: {
                direction: direction,
                id: $(this).parent().attr("id"),
                url: encodeURI(query)
            }
        })
            .done(function( html ) {
                console.log(html);
            });
        var voteCount = $(this).parent().children(".vote-count");
        voteCount.text(parseInt(voteCount.text()) + offset); // update the displayed vote count
        $(this).toggleClass("vb-clicked"); // remember whether this button was clicked or not
    }

    $tagButtons.on("mousedown", showTagDropdown);

    $document.on("mousedown", hideTagDropdown);

    $tagItems.on("mousedown", toggleTag);

    $filterInput.on("input", filterTags)

    $voteButtons.on("click", updateVote);
});