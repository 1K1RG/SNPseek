$(document).ready(function() {
	var active = 1;
	var result_active = 1;

	$('#mainSidebar').on('click', function() {


		if (active == 1) {
			$("#leftsideBarInc").css({
				'width': '70px'
			});

			$("#leftsidebarTemplate").css({
				'width': '70px'
			});

			$("#sidebarId").css({
				'width': '70px'
			});

			$("#leftsidebarTemplate").css({
				'width': '70px'
			});

			$("#genotypeDiv").css({
				'height': '40px'
			});
			$("#genotypeLink").css({
				'height': '50px'
			});

			$("#genotypeIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#genotypeSpan").css({
				'opacity': '0',
				'visibility': 'hidden',
			});


			$("#varietyDiv").css({
				'height': '40px'
			});
			$("#varietyLink").css({
				'height': '50px'
			});

			$("#varietyIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#varietySpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#geneLociDiv").css({
				'height': '40px'
			});
			$("#geneLociLink").css({
				'height': '50px'
			});

			$("#geneLociIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#geneLociSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#jbrowseDiv").css({
				'height': '40px'
			});
			$("#jbrowseLink").css({
				'height': '50px'
			});

			$("#jbrowseIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#jbrowseSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#gwasDiv").css({
				'height': '40px'
			});
			$("#gwasLink").css({
				'height': '50px'
			});

			$("#gwasIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#gwasSpan").css({
				'opacity': '0',
				'visibility': 'hidden',

			});

			$("#myListDiv").css({
				'height': '40px'
			});
			$("#myListLink").css({
				'height': '50px'
			});

			$("#myListIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#myListSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#downloadDiv").css({
				'height': '40px'
			});

			$("#downloadLink").css({
				'height': '50px'
			});

			$("#downloadIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#downloadSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#helpDiv").css({
				'height': '40px'
			});

			$("#helpLink").css({
				'height': '50px'
			});

			$("#helpIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#helpSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});


			active = 0;

		} else {

			$("#leftsideBarInc").css({
				'width': '90px'
			});
			$("#leftsidebarTemplate").css({
				'width': '90px'
			});

			$("#sidebarId").css({
				'width': '90px'
			});

			$("#leftsidebarTemplate").css({
				'width': '90px'
			});

			$("#genotypeDiv").css({
				'height': '70px'
			});
			$("#genotypeLink").css({
				'height': '70px'
			});
			$("#genotypeIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#genotypeSpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '-5px'
			});

			$("#varietyDiv").css({
				'height': '70px'
			});
			$("#varietyLink").css({
				'height': '70px'
			});
			$("#varietyIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#varietySpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '3px'
			});

			$("#geneLociDiv").css({
				'height': '70px'
			});
			$("#geneLociLink").css({
				'height': '70px'
			});
			$("#geneLociIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#geneLociSpan").css({
				'opacity': '1',
				'visibility': 'visible'
			});

			$("#jbrowseDiv").css({
				'height': '70px'
			});
			$("#jbrowseLink").css({
				'height': '70px'
			});
			$("#jbrowseIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#jbrowseSpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '3px'
			});

			$("#gwasDiv").css({
				'height': '70px'
			});
			$("#gwasLink").css({
				'height': '70px'
			});
			$("#gwasIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#gwasSpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '7px'
			});

			$("#myListDiv").css({
				'height': '70px'
			});
			$("#myListLink").css({
				'height': '70px'
			});
			$("#myListIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#myListSpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '7px '
			});

			$("#downloadDiv").css({
				'height': '70px'
			});
			$("#downloadLink").css({
				'height': '70px'
			});
			$("#downloadIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#downloadSpan").css({
				'opacity': '1',
				'visibility': 'visible'
				
			});

			$("#helpDiv").css({
				'height': '70px'
			});
			$("#helpLink").css({
				'height': '70px'
			});
			$("#helpIcon").css({
				'font-size': '30px',
				'margin-left': '10px'
			});
			$("#helpSpan").css({
				'opacity': '1',
				'visibility': 'visible',
				'padding-left': '9px '
			});


			active = 1;
		}

	});

	$('#sidebarCollapse3').on('click', function() {
		$('#genotypeSearchSidebar').removeClass('active');
	});

	$('#queryDiv2').on('click', function() {

		alert("toggle Div active");


	});

	$('#step1Div').hover(function() {
		$("#varietyInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#varietyInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#step2Div').hover(function() {
		$("#regionInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#regionInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#step3Div').hover(function() {
		$("#optionInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#optionInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#finalStepDiv').hover(function() {
		$("#buttonBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#buttonBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#chrInput').hover(function() {
		$("#startInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});
		$("#endInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});
		$("#chrInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});

	}, function() {
		$("#startInput").css({
			'border': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
		$("#endInput").css({
			'border-color': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
		$("#chrInput").css({
			'border-color': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
	});



	$('#closeSidebar').on('click', function() {
		$('#genotypeSearchSidebar').addClass('active');
	});


	$("#bodyContent").scroll(function() {
		alert("test");
	});


});

$(window).on("load", function() {
	


			$("#leftsideBarInc").css({
				'width': '70px'
			});

			$("#leftsidebarTemplate").css({
				'width': '70px'
			});

			$("#sidebarId").css({
				'width': '70px'
			});

			$("#leftsidebarTemplate").css({
				'width': '70px'
			});

			$("#genotypeDiv").css({
				'height': '40px'
			});
			$("#genotypeLink").css({
				'height': '50px'
			});

			$("#genotypeIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#genotypeSpan").css({
				'opacity': '0',
				'visibility': 'hidden',
			});


			$("#varietyDiv").css({
				'height': '40px'
			});
			$("#varietyLink").css({
				'height': '50px'
			});

			$("#varietyIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#varietySpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#geneLociDiv").css({
				'height': '40px'
			});
			$("#geneLociLink").css({
				'height': '50px'
			});

			$("#geneLociIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#geneLociSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#jbrowseDiv").css({
				'height': '40px'
			});
			$("#jbrowseLink").css({
				'height': '50px'
			});

			$("#jbrowseIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#jbrowseSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#gwasDiv").css({
				'height': '40px'
			});
			$("#gwasLink").css({
				'height': '50px'
			});

			$("#gwasIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#gwasSpan").css({
				'opacity': '0',
				'visibility': 'hidden',

			});

			$("#myListDiv").css({
				'height': '40px'
			});
			$("#myListLink").css({
				'height': '50px'
			});

			$("#myListIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#myListSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#downloadDiv").css({
				'height': '40px'
			});

			$("#downloadLink").css({
				'height': '50px'
			});

			$("#downloadIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#downloadSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});

			$("#helpDiv").css({
				'height': '40px'
			});

			$("#helpLink").css({
				'height': '50px'
			});

			$("#helpIcon").css({
				'font-size': '24px',
				'margin-left': '0px'
			});
			$("#helpSpan").css({
				'opacity': '0',
				'visibility': 'hidden'
			});




});



