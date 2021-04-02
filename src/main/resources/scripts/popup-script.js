runOnPopup();

$(function() {
    $("#whichTechnologiesToSee").select2()

    $("#selectedTechnologiesClean").on('click', function (event) {
        $("#whichTechnologiesToSee").val(null).trigger('change')
    })
});
