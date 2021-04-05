runOnPopup();

$(function() {
    $("#whichTechnologiesToSee").select2()

    $("#selectedTechnologiesCleanButton").on('click', function (event) {
        $("#whichTechnologiesToSee").val(null).trigger('change')
    })
});
