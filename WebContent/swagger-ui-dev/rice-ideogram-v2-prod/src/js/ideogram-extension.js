$(document).ready(function () {
  searchEnterOverride();
  collapsibleArrowAnimate();
  setUpBrush();
});
var panZoomTiger,
  isZoomOn = !0,
  isPanOn = !0,
  initialize = !1;
function setUpZoomButtons() {
  var a = document.querySelector("#ideogram");
  panZoomTiger = svgPanZoom(a, {
    panEnabled: !1,
    controlIconsEnabled: !0,
    mouseWheelZoomEnabled: !0,
    zoomScaleSensitivity: 0.5,
    minZoom: 1,
    maxZoom: 5,
  });
  $("g#svg-pan-zoom-zoom-in").attr(
    "transform",
    "translate(-250.5 33) scale(0.027)",
  );
  $("g#svg-pan-zoom-zoom-out").attr(
    "transform",
    "translate(-200.5 33) scale(0.027)",
  );
  $("g#svg-pan-zoom-reset-pan-zoom").attr(
    "transform",
    "translate(-150.5 28.5) scale(0.7)",
  );
  panZoomTiger.disableZoom();
  panZoomTiger.disablePan();
}
function toggleZoom() {
  initialize || (setUpZoomButtons(), (initialize = !initialize));
  isZoomOn
    ? (panZoomTiger.enableZoom(),
      panZoomTiger.enablePan(),
      $("#enable-zoom button").text("Zoom enabled"),
      $("g#svg-pan-zoom-controls, #enable-pan button").css(
        "visibility",
        "visible",
      ))
    : (panZoomTiger.disableZoom(),
      panZoomTiger.disablePan(),
      $("#enable-zoom button").text("Zoom disabled"),
      $("g#svg-pan-zoom-controls, #enable-pan button").css(
        "visibility",
        "hidden",
      ));
  isZoomOn = !isZoomOn;
}
function togglePan() {
  isPanOn
    ? (panZoomTiger.enablePan(), $("#enable-pan button").text("Pan enabled"))
    : (panZoomTiger.disablePan(), $("#enable-pan button").text("Pan disabled"));
  isPanOn = !isPanOn;
}
function setUpBrush() {
  adjustIdeogramSVG();
  dropdownMenuSetup();
  $("g#svg-pan-zoom-controls, #enable-pan button").css("visibility", "hidden");
}
var brushID,
  xCoordinates = [],
  isMenuOpen = [],
  previousBrush = "some-id-i-used-to-know",
  brushExtent = [],
  arrayOfColorsBrushes = [];
function adjustIdeogramSVG() {
  $("#ideogram").attr("width", "1000");
  $("#ideogram").attr("height", "900");
  $(".background").css("cursor", "zoom-in");
}
function dropdownMenuSetup() {
  $(".dynamic-dropdown").wrapInner(dropdownMenuForm);
  $("ul.file_menu").stop(!0, !0).slideUp(1);
  $(".extent").hover(
    function (a) {
      brushID = $(this).parent().attr("id");
      "some-id-i-used-to-know" === previousBrush
        ? $("#" + previousBrush).attr("id", "some-id-" + brushID)
        : $("#some-id-" + previousBrush).attr("id", "some-id-" + brushID);
      previousBrush = brushID;
      for (var c = 0, b = 0; b < ideogram.numChromosomes; b++)
        isBrushActive[b] && c++;
      1 < c
        ? ($("#defaultOpen").attr("class", "inactive-link tablinks"),
          $(".show-jbrowse").attr("class", "inactive-link show-jbrowse"))
        : ($("#defaultOpen").attr("class", "active-link tablinks"),
          $(".show-jbrowse").attr("class", "active-link show-jbrowse"));
      c = parseInt(brushID.replace(/[^0-9\.]/g, ""), 10);
      b = ideogram.chromosomesArray[c].bands[1].bp.stop;
      $("#chr-name").text("chr " + (c + 1) + " | ");
      $("#chr-name-max").text(" " + b);
      $("#startBPTextbox").val(selectedRegion[c].from);
      $("#endBPTextbox").val(selectedRegion[c].to);
      $(".file_menu").css("display", "visible");
      $(".dynamic-dropdown").attr(
        "transform",
        "translate(" + (a.pageX - 270) + ", " + (a.pageY - 50) + ")",
      );
      $("ul.file_menu").stop(!0, !0).slideDown("medium");
      $(".identify-the-brush").attr("id", brushID);
      $(".submit-chr-details").attr("id", brushID);
      $(".show-jbrowse").attr("id", brushID);
      $("#" + $(this).parent().attr("id")).css("visibility", "visible");
      isMenuOpen[c] = !0;
    },
    function () {},
  );
  $(".dynamic-dropdown").hover(
    function () {},
    function () {
      $("ul.file_menu").stop(!0, !0).slideUp("medium");
      $("#startBPTextbox").val("");
      $("#endBPTextbox").val("");
      $("#message-input-menu").text("");
      $("#startBPTextbox").css("background-color", "white");
      $("#startBPTextbox").css("color", "black");
      $("#endBPTextbox").css("background-color", "white");
      $("#endBPTextbox").css("color", "black");
    },
  );
}
function redirectToJBrowse(a) {
  $("html, body").animate(
    { scrollTop: $(document).height(), scrolllLft: 0 },
    "slow",
  );
  document.getElementById("defaultOpen").click();
  $("#no-content-jb").remove();
  $("#goToTable").attr("class", "inactive-link tablinks");
  $("#JBrowseView").css("height", "460");
  a = parseInt(a.replace(/[^0-9\.]/g, ""), 10) + 1;
  var c = $("#startBPTextbox").val(),
    b = $("#endBPTextbox").val(),
    c = c + ".." + b;
  a = 10 > a ? "?loc=chr0" + a + "%3A" : "?loc=chr" + a + "%3A";
  $("#jbrowse").prop(
    "src",
    "https://snpseekv3.irri-e-extension.com/jbrowse/" + a + c + "&tracks=DNA",
  );
  $("#jbrowse").show();
  $("#goToTable").attr("class", "active-link tablinks");
}
function deleteAllBrush() {
  $(".extent").attr("height", "0");
  $("ul.file_menu").stop(!0, !0).slideUp("medium");
  for (var a = 0; a < ideogram.numChromosomes; a++) isBrushActive[a] = !1;
}
function deleteThisBrush(a) {
  $("#" + a + " > .extent").attr("height", "0");
  $("ul.file_menu").stop(!0, !0).slideUp("medium");
  isBrushActive[parseInt(a.replace(/[^0-9\.]/g, ""), 10)] = !1;
}
function setTheBrush(a) {
  var c = $("#startBPTextbox").val(),
    b = $("#endBPTextbox").val(),
    d = parseInt(a.replace(/[^0-9\.]/g, ""), 10),
    d = ideogram.chromosomesArray[d].bands[1].bp.stop;
  "" === c && "" === b
    ? ($("#startBPTextbox").css("background-color", "#EF5350"),
      $("#startBPTextbox").css("color", "#EEEEEE"),
      $("#endBPTextbox").css("background-color", "#EF5350"),
      $("#endBPTextbox").css("color", "#EEEEEE"),
      $("#message-input-menu").text(
        "Empty input field for start and end range.",
      ),
      $("#message-input-menu").css("color", "#EF5350"))
    : "" === c
      ? ($("#startBPTextbox").css("background-color", "#EF5350"),
        $("#startBPTextbox").css("color", "#EEEEEE"),
        $("#endBPTextbox").css("background-color", "white"),
        $("#endBPTextbox").css("color", "black"),
        $("#message-input-menu").text("Empty input field for start range."),
        $("#message-input-menu").css("color", "#EF5350"))
      : "" === b
        ? ($("#startBPTextbox").css("background-color", "white"),
          $("#startBPTextbox").css("color", "black"),
          $("#endBPTextbox").css("background-color", "#EF5350"),
          $("#endBPTextbox").css("color", "#EEEEEE"),
          $("#message-input-menu").text("Empty input field for end range."),
          $("#message-input-menu").css("color", "#EF5350"))
        : parseInt(c, 10) > parseInt(b, 10) && 0 < c.length && 0 < b.length
          ? ($("#startBPTextbox").css("background-color", "#EF6C00"),
            $("#startBPTextbox").css("color", "#EEEEEE"),
            $("#endBPTextbox").css("background-color", "#EF6C00"),
            $("#endBPTextbox").css("color", "#EEEEEE"),
            $("#message-input-menu").text(
              "Invalid range. Please check your start and end base pairs.",
            ),
            $("#message-input-menu").css("color", "#EF6C00"))
          : parseInt(b, 10) > d
            ? ($("#startBPTextbox").css("background-color", "white"),
              $("#startBPTextbox").css("color", "black"),
              $("#endBPTextbox").css("background-color", "#FF5722"),
              $("#endBPTextbox").css("color", "#EEEEEE"),
              $("#message-input-menu").text(
                "The end range exceeded the chromosome length.",
              ),
              $("#message-input-menu").css("color", "#FF5722"))
            : ($("#startBPTextbox").css("background-color", "white"),
              $("#startBPTextbox").css("color", "black"),
              $("#endBPTextbox").css("background-color", "white"),
              $("#endBPTextbox").css("color", "black"),
              $("#message-input-menu").text(""),
              (d = parseInt(a.replace(/[^0-9\.]/g, ""), 10)),
              (d = arrayOfBrushes[d]),
              d3.select("#" + a).call(d.clear()),
              d3
                .select("#" + a)
                .call(d.extent([parseInt(c, 10), parseInt(b, 10)])));
}
function countActiveBrushes() {
  for (var a = 0, c = 0; c < ideogram.numChromosomes; c++)
    isBrushActive[c] && (a += 1);
  return a;
}
var isHeaderPresent = !1;
function showStatiscalTable(a) {
  $("#defaultOpen").attr("class", "inactive-link tablinks");
  document.getElementById("goToTable").click();
  $("#no-content-gt").remove();
  $("#GeneTable").css("height", "440");
  $(".show-genes").attr("class", "inactive-link show-genes");
  $("html, body").animate(
    { scrollTop: $(document).height(), scrollLeft: 0 },
    "slow",
  );
  $("#gene-table-content").empty();
  var c = "start=" + $("#startBPTextbox").val() + "&",
    b = "end=" + $("#endBPTextbox").val();
  a = document.getElementById("gt-div");
  var d = new Spinner({
      lines: 9,
      length: 9,
      width: 14,
      radius: 20,
      scale: 1,
      corners: 1,
      color: "#000",
      opacity: 0.25,
      rotate: 0,
      direction: 1,
      speed: 1,
      trail: 60,
      fps: 20,
      zIndex: 2e9,
      className: "gt-spinner",
      top: "-100px",
      left: "38%",
      shadow: !0,
      hwaccel: !1,
      position: "absolute",
    }).spin(a),
    f = 0,
    e = 0,
    f = countActiveBrushes();
  for (a = 0; a < ideogram.numChromosomes; a++)
    if (isBrushActive[a]) {
      var g = 9 > a ? "chr0" + (a + 1) + "?" : "chr" + (a + 1) + "?";
      b = arrayOfBrushes[a].extent();
      c = Math.floor(b[0]);
      b = Math.ceil(b[1]);
      c = "start=" + c + "&";
      b = "end=" + b;
      g =
        "https://snpseekv3.irri-e-extension.com/ws/genomics/gene/osnippo/" +
        g +
        c +
        b;
      $.ajax({
        dataType: "json",
        crossDomain: !0,
        url: g,
        data: void 0,
        success: function (a) {
          buildHtmlTable(a, "#gene-table-content");
          e += 1;
          $("#defaultOpen").attr("class", "active-link tablinks");
          $(".show-genes").attr("class", "active-link show-genes");
          toggleSpinner(d, !1);
          isHeaderPresent = !0;
          e == f &&
            ($("#GeneTable").css("height", "480"),
            $("table").tableExport({
              headings: !0,
              footers: !0,
              formats: ["xls", "csv", "txt"],
              fileName: "gene-table",
              emptyCSS: ".tableexport-empty",
              trimWhitespace: !1,
            }),
            (isHeaderPresent = !1));
        },
      });
    }
}
function buildHtmlTable(a, c) {
  for (
    var b = addAllColumnHeaders(a, c, isHeaderPresent),
      d = $("<tbody/>"),
      f = 0;
    f < a.length;
    f++
  ) {
    for (var e = $("<tr/>"), g = 0; g < b.length; g++) {
      var h = a[f][b[g]];
      null == h
        ? e.append($("<td/>").html(""))
        : ((h = String(h)),
          (h = h.replace(/,/g, "<br>")),
          e.append($("<td/>").html(h)));
    }
    isHeaderPresent ? $(c + " tBody").append(e) : $(d).append(e);
  }
  isHeaderPresent || $(c).append(d);
}
function addAllColumnHeaders(a, c, b) {
  for (
    var d = [], f = $("<tr/>"), e = $("<thead/>"), g = 0;
    g < a.length;
    g++
  ) {
    var h = a[g],
      k;
    for (k in h)
      -1 == $.inArray(k, d) && (d.push(k), f.append($("<th/>").html(k)));
  }
  b || ($(e).append(f), $(c).append(e));
  return d;
}
var annotObject = {},
  annotArray = [],
  activeURLs = 0,
  counterURLs = 0,
  geneQueryCount = 0,
  searchQueryAnnot = 0,
  brushSelectionCount = 0,
  geneQueryCountArray = [],
  isCheckboxPresent = !1;
function generateGeneAnnotURLs(a) {
  $("#startBPTextbox").val();
  $("#endBPTextbox").val();
  for (var c = 0; c < ideogram.numChromosomes; c++)
    if (isBrushActive[c]) {
      activeURLs += 1;
      console.log("active brush " + (c + 1));
      var b = 9 > c ? "chr0" + (c + 1) + "?" : "chr" + (c + 1) + "?";
      var d = arrayOfBrushes[c].extent();
      var f = Math.ceil(d[1]);
      d = "start=" + Math.floor(d[0]) + "&";
      f = "end=" + f;
      a[c] =
        "https://snpseekv3.irri-e-extension.com/ws/genomics/gene/osnippo/" +
        b +
        d +
        f;
    } else a[c] = null;
  return a;
}
function processCollectedAnnots(a, c) {
  d3.json(a, function (a, d) {
    a ? c(null) : 0 >= d.length ? c(null) : c(d);
  });
}
function appendCheckbox(a, c) {
  var b =
    '<div class="color-block" id="color-block-' +
    (geneQueryCount + 59) +
    '" style="background-color: ' +
    arrayOfColorsBrushes[geneQueryCount] +
    ' "></div>';
  var d = a + "-" + (geneQueryCount + 59);
  $(
    "<li>" +
      ('<input type="checkbox" onclick="toggleFilter(this)"id="' +
        d +
        '" tracks="' +
        d +
        '"></input>') +
      ("<label for='" + d + "'>" + b + a + "-" + c + "</label>") +
      "</li>",
  ).appendTo("#category-content-gq ul");
  $("#" + d).prop("checked", !0);
  geneQueryCount += 1;
  /search/.test(a) ? (searchQueryAnnot += 1) : (brushSelectionCount += 1);
}
function addAnnotationLinks() {
  d3.selectAll(".annot").on("click", function (a, c) {
    toggleLinearScale("visible");
    var b = a.start.toString() + ".." + (a.start + a.length).toString(),
      d = "&tracks=DNA%2C" + ideogram.config.selectedTrack + "&highlight=";
    var f =
      10 > a.chr ? "?loc=chr0" + a.chr + "%3A" : "?loc=chr" + a.chr + "%3A";
    console.log(
      "src",
      "https://snpseekv3.irri-e-extension.com/jbrowse/" + f + b + d,
    );
    $("#jbrowse").prop(
      "src",
      "https://snpseekv3.irri-e-extension.com/jbrowse/" + f + b + d,
    );
    $("#jbrowse").show();
  });
}
function formWebServicePlot() {
  for (var a = "", c = 0; c < ideogram.numChromosomes; c++)
    if (isBrushActive[c]) {
      var b = arrayOfBrushes[c].extent(),
        d = Math.floor(b[0]),
        b = Math.ceil(b[1]);
      console.log("active brush " + (c + 1));
      a =
        (9 > c ? "chr0" + (c + 1) : "chr" + (c + 1)) +
        "-" +
        d +
        "-" +
        b +
        "," +
        a;
      console.log(a);
    }
  console.log(
    "https://snpseekv3.irri-e-extension.com/ws/genomics/gene/osnippo?region=" +
      a,
  );
  return (
    "https://snpseekv3.irri-e-extension.com/ws/genomics/gene/osnippo?region=" +
    a
  );
}
function configureBrushAnnot(a, c, b, d) {
  a = processedAnnotsObj[a].contents;
  d3.select("#ideogram").remove();
  for (var f = [], e = 0; e < ideogram.numChromosomes; e++) {
    var g = ["1"];
    g.splice(0, 1);
    f.push({ chrNum: (e + 1).toString(), data: g });
  }
  for (e = 0; e < a.length; e++) {
    var g = transformToNCList(a[e]),
      h = parseInt(String(a[e].contig).replace(/[^0-9\.]/g, ""), 10) - 1;
    f[h].data.push(g);
  }
  for (e = 0; e < f.length; e++) traitData[e] = f[e];
  d = null === d ? "brush-selection-" + (geneQueryCount + 59) : d;
  b &&
    (addTrack(d),
    appendCheckbox("brush-selection", brushSelectionCount),
    addColorToAnnotationTracks(d, c));
  config.isSearchBrush = !0;
  config.annotationsColor = c;
  config.rawAnnots = reformatTraitData(d);
  config.selectedTrack = d;
  config.allTracks = allTracks;
  b &&
    ((ideogram = new Ideogram(config)),
    setUpBrush(),
    setUpZoomButtons(),
    (config.isSearchBrush = !1));
}
function plotGeneAnnotation() {
  $(".plot-genes").attr("class", "inactive-link plot-genes");
  var a = document.getElementById("chromosome-render"),
    c = new Spinner({
      lines: 9,
      length: 9,
      width: 14,
      radius: 20,
      scale: 1,
      corners: 1,
      color: "#000",
      opacity: 0.25,
      rotate: 0,
      direction: 1,
      speed: 1,
      trail: 60,
      fps: 20,
      zIndex: 2e9,
      className: "spinner",
      top: "20%",
      left: "25%",
      shadow: !0,
      hwaccel: !1,
      position: "absolute",
    }).spin(a);
  var b = formWebServicePlot();
  var d = (ideogram.config.annotationsColor = getRandomColor());
  arrayOfColorsBrushes.push(ideogram.config.annotationsColor);
  asyncLoop({
    length: 1,
    functionToLoop: function (a, e) {
      setTimeout(function () {
        processCollectedAnnots(b, function (a) {
          console.log(a);
          var e = {};
          e.id = "brush-selection-" + (brushSelectionCount + 59);
          e.contents = a;
          processedAnnotsObj[brushSelectionCount] = e;
          configureBrushAnnot(brushSelectionCount, d, !0, null);
          putSearchToTable(b, d);
          $("#searchbox-keyword-message").text(
            "Annotations are in this color.",
          );
          $("#searchbox-keyword-message").css("color", d);
          $(".plot-genes").attr("class", "active-link plot-genes");
          toggleSpinner(c, !1);
        });
        a();
      }, 100);
    },
    callback: function () {},
  });
}
function getRandomColor() {
  for (var a = "#", c = 0; 6 > c; c++)
    a += "0123456789ABCDEF"[Math.floor(16 * Math.random())];
  return a;
}
function searchEnterOverride() {
  $("#search-keyword").bind("keypress", {}, function (a) {
    13 == (a.keyCode ? a.keyCode : a.which) &&
      (a.preventDefault(), triggerSearchBox());
  });
}
function fixAnnotChr(a) {
  for (var c = a["0"].annots, b = 0; b < c.length; b++)
    (number = parseInt(c[b].chrName.replace(/[^0-9\.]/g, ""), 10)),
      (a["0"].annots[b].chr = number);
  a = [];
  for (b = 0; b < ideogram.numChromosomes; b++) {
    var d = ["1"];
    d.splice(0, 1);
    a.push({ chr: (b + 1).toString(), annots: d });
  }
  for (b = 0; b < c.length; b++)
    for (var d = c[b], f = 0; f < ideogram.numChromosomes; f++)
      a[f].chr == d.chr && ((d.chrIndex = d.chr - 1), a[f].annots.push(d));
  return a;
}
function transformToNCList(a) {
  var c = [];
  c.push(1);
  c.push(a.fmin);
  c.push(a.fmax);
  c.push(1);
  c.push(1);
  c.push(1);
  c.push(1);
  c.push(a.uniquename + "\n" + a.description);
  return c;
}
function putSearchToTable(a, c) {
  $.ajax({
    dataType: "json",
    crossDomain: !0,
    url: a,
    data: void 0,
    success: function (a) {
      buildHtmlTable(a, "#gene-table-content");
      $("#defaultOpen").attr("class", "active-link tablinks");
      $(".show-genes").attr("class", "active-link show-genes");
      $("input#search-keyword").prop("disabled", !1);
      $("#search-button").prop("disabled", !1);
      addAnnotationLinks();
      $("#GeneTable").css("height", "480");
      $("table").tableExport({
        headings: !0,
        footers: !0,
        formats: ["xls", "csv", "txt"],
        fileName: "gene-table",
        emptyCSS: ".tableexport-empty",
        trimWhitespace: !1,
      });
    },
  });
}
function addColorToAnnotationTracks(a, c) {
  var b = {};
  b.id = a;
  b.color = c;
  config.annotationTracks.push(b);
  console.log("size of color storage: " + config.annotationTracks.length);
  console.log("index of new color: " + config.annotationTracks.indexOf(b));
  console.log(config.annotationTracks);
}
function configureSearchAnnot(a, c, b, d) {
  console.log(processedAnnotsObj[a]);
  a = processedAnnotsObj[a].contents;
  d3.select("#ideogram").remove();
  for (var f = [], e = 0; e < ideogram.numChromosomes; e++) {
    var g = ["1"];
    g.splice(0, 1);
    f.push({ chrNum: (e + 1).toString(), data: g });
  }
  for (e = 0; e < a.length; e++) {
    var g = transformToNCList(a[e]),
      h = parseInt(String(a[e].contig).replace(/[^0-9\.]/g, ""), 10);
    f[h].data.push(g);
  }
  for (e = 0; e < f.length; e++) traitData[e] = f[e];
  d = null === d ? "search-query-" + (geneQueryCount + 59) : d;
  b &&
    (addTrack(d),
    appendCheckbox($("#search-keyword").val(), searchQueryAnnot),
    addColorToAnnotationTracks(d, c));
  config.isSearchBrush = !0;
  config.annotationsColor = c;
  config.rawAnnots = reformatTraitData(d);
  config.selectedTrack = d;
  config.allTracks = allTracks;
  b &&
    ((ideogram = new Ideogram(config)),
    setUpBrush(),
    setUpZoomButtons(),
    (config.isSearchBrush = !1));
}
function triggerSearchBox() {
  $("#defaultOpen").attr("class", "inactive-link tablinks");
  document.getElementById("goToTable").click();
  $("#no-content-gt").remove();
  $("#GeneTable").css("height", "440");
  $("input#search-keyword").prop("disabled", !0);
  $("#search-button").prop("disabled", !0);
  $("#gene-table-content").empty();
  $("#searchbox-keyword-message").text("");
  var a =
      "https://snpseekv3.irri-e-extension.com/ws/genomics/gene/osnippo/search/word/" +
      $("#search-keyword").val(),
    c = document.getElementById("chromosome-render"),
    b = new Spinner({
      lines: 9,
      length: 9,
      width: 14,
      radius: 20,
      scale: 1,
      corners: 1,
      color: "#000",
      opacity: 0.25,
      rotate: 0,
      direction: 1,
      speed: 1,
      trail: 60,
      fps: 20,
      zIndex: 2e9,
      className: "spinner",
      top: "20%",
      left: "25%",
      shadow: !0,
      hwaccel: !1,
      position: "absolute",
    }).spin(c);
  var d = (ideogram.config.annotationsColor = getRandomColor());
  arrayOfColorsBrushes.push(ideogram.config.annotationsColor);
  asyncLoop({
    length: 1,
    functionToLoop: function (c, e) {
      setTimeout(function () {
        processCollectedAnnots(a, function (c) {
          if (null === c)
            $("#searchbox-keyword-message").text("No results found."),
              $("#GeneTable").css("height", "140"),
              $("#GeneTable").append(
                '<div id="no-content-gt"><h3>Gene table has no results yet.</h3><p>You can get to view genomic data by performing one of the following:</p><p class="li-content">- Searching a keyword</p><p class="li-content">- Create a brush, then click "Plot all genes" to view the results.</p></div>',
              ),
              $("#defaultOpen").attr("class", "active-link tablinks"),
              $(".show-genes").attr("class", "active-link show-genes"),
              $("input#search-keyword").prop("disabled", !1),
              $("#search-button").prop("disabled", !1);
          else {
            var e = {};
            e.id = "search-query-" + (geneQueryCount + 59);
            e.contents = c;
            processedAnnotsObj[geneQueryCount] = e;
            configureSearchAnnot(geneQueryCount, d, !0, null);
            putSearchToTable(a, d);
            $("#searchbox-keyword-message").text(
              "Annotations are in this color.",
            );
            $("#searchbox-keyword-message").css("color", d);
          }
          toggleSpinner(b, !1);
        });
        c();
      }, 100);
    },
    callback: function () {},
  });
}
var isNightMode = !0;
function turnNightMode() {
  $("style").detach();
  if (isNightMode) {
    var a =
      "<style>body{background-color: #363636;color: #EEEEEE;}\n.color-block{outline-color: #363636;background-color: #363636;}\n#search-keyword{color: #EEEEEE;background-color: #363636;}\n.domain{stroke: #EEEEEE !important;}\n.tick text{fill: #EEEEEE;}\n.tick line{stroke: #EEEEEE !important;}\n.nightmodebutton button{background-color: #FAFAFA;color: black;}\n.acen{fill: #80CBC4 !important;}\n.chromosome text{fill: #EEEEEE;}\n.file_menu{background-color: #BDBDBD;}\n.white-text{color: #363636;}\n.white-text-default{color: #363636;}\n.white-text-smaller{color: #363636;}\n.submit-chr-details{color: #363636;background-color: #EEEEEE;}\n.file_menu li a {color: #363636;}\n#form-render::-webkit-scrollbar-track,#form-render-qtl::-webkit-scrollbar-track,#form-render-brush::-webkit-scrollbar-track{background-color: #303030;}\n#gene-table-content tbody::-webkit-scrollbar-thumb,#form-render::-webkit-scrollbar-thumb,#form-render-qtl::-webkit-scrollbar-thumb,#form-render-brush::-webkit-scrollbar-thumb{background-color: #80CBC4;}\n.table thead{background-color: #424242;}\n.table tbody{background-color: #616161;}\n</style>";
    $(".nightmodebutton button").text("Day mode");
    $(document.head).append(a);
    ideogram.config.annotationTracks = nightModeColor;
    ideogram.config.annotationsColor = "white";
  } else
    (a =
      "<style>body{background-color: white;color: black;}\n.color-block{outline-color: white;background-color: white;}\n#search-keyword{color: black;background-color: white;}\n.domain{stroke: #000 !important;}\n.tick text{fill: #000;}\n.tick line{stroke: #000 !important;}\n.nightmodebutton button{background-color: #757575;color: white;}\n.acen{fill: #FDD !important;}\n.chromosome text{fill: #000;}\n.file_menu{background-color: #212121;}\n.white-text{color: #E0E0E0;}\n.white-text-default{color: #E0E0E0;}\n.white-text-smaller{color: #E0E0E0;}\n.submit-chr-details{color: #EEEEEE;background-color: #424242;}\n.file_menu li a {color: #FFFFFF;}\n#form-render::-webkit-scrollbar,#form-render-qtl::-webkit-scrollbar,#form-render-brush::-webkit-scrollbar{background-color: white;}\n#gene-table-content tbody::-webkit-scrollbar-thumb,#form-render::-webkit-scrollbar-thumb,#form-render-qtl::-webkit-scrollbar-thumb,#form-render-brush::-webkit-scrollbar-thumb{background-color: #FDD;}\n.table thead{background-color: #212121;}\n.table tbody{background-color: #303030;}\n</style>"),
      $(".nightmodebutton button").text("Night mode"),
      $(document.head).append(a),
      (ideogram.config.annotationTracks = defaultColor),
      (ideogram.config.annotationsColor = "black");
  isNightMode = !isNightMode;
}
var emptyJB = !0,
  emptyGT = !0;
function toggleResult(a, c) {
  $("#jbrowse").is(":empty") && emptyJB
    ? ($("#JBrowseView").css("height", "140"),
      $("#JBrowseView").append(
        '<div id="no-content-jb"><h3>JBrowse is not active yet.</h3><p>To make it appear, you can do either perform one of the following:</p><p class="li-content">- Click on an annotation.</p><p class="li-content">- Create a brush, then click "Show in JBrowse."</p></div>',
      ),
      (emptyJB = !emptyJB))
    : $("#jbrowse").is(":empty") ||
      ($("#no-content-jb").remove(), $("#JBrowseView").css("height", "460"));
  $("#gene-table-content").is(":empty") && emptyGT
    ? ($("#GeneTable").css("height", "140"),
      $("#GeneTable").append(
        '<div id="no-content-gt"><h3>Gene table has no results yet.</h3><p>You can get to view genomic data by performing one of the following:</p><p class="li-content">- Searching a keyword</p><p class="li-content">- Create a brush, then click "Plot all genes" to view the results.</p></div>',
      ),
      (emptyGT = !emptyGT))
    : $("#gene-table-content").is(":empty") ||
      ($("#no-content-gt").remove(), $("#GeneTable").css("height", "440"));
  var b;
  var d = document.getElementsByClassName("tabcontent");
  for (b = 0; b < d.length; b++) d[b].style.display = "none";
  d = document.getElementsByClassName("tablinks");
  for (b = 0; b < d.length; b++)
    d[b].className = d[b].className.replace(" active", "");
  document.getElementById(c).style.display = "block";
  a.currentTarget.className += " active";
}
function anotherTab(a, c) {
  var b;
  var d = document.getElementsByClassName("tabcontent-anotherone");
  for (b = 0; b < d.length; b++) d[b].style.display = "none";
  d = document.getElementsByClassName("tablinks-anotherone");
  for (b = 0; b < d.length; b++)
    d[b].className = d[b].className.replace(" active", "");
  document.getElementById(c).style.display = "block";
  a.currentTarget.className += " active";
}
function showJBrowseAnnotClick() {
  $(".annot").click(function () {
    $("html, body").animate(
      { scrollTop: $(document).height(), scrollLeft: 0 },
      "slow",
    );
    document.getElementById("defaultOpen").click();
    $("#JBrowseView").css("height", "460");
    $("#no-content-jb").remove();
  });
}
function collapsibleArrowAnimate() {
  var a = !0,
    c = !0,
    b = !0;
  jQuery.fn.rotate = function (a) {
    $(this).css({
      "-webkit-transform": "rotate(" + a + "deg)",
      "-moz-transform": "rotate(" + a + "deg)",
      "-ms-transform": "rotate(" + a + "deg)",
      transform: "rotate(" + a + "deg)",
    });
  };
  $("#collapsible-tg").click(function () {
    a
      ? ($("#arrow-tg").rotate(0),
        $("#collapsible-tg").css("background-color", "#E0E0E0"))
      : ($("#arrow-tg").rotate(-90),
        $("#collapsible-tg").css("background-color", "#EEEEEE"));
    a = !a;
  });
  $("#collapsible-qtl").click(function () {
    c
      ? ($("#arrow-qtl").rotate(0),
        $("#collapsible-qtl").css("background-color", "#E0E0E0"))
      : ($("#arrow-qtl").rotate(-90),
        $("#collapsible-qtl").css("background-color", "#EEEEEE"));
    c = !c;
  });
  $("#collapsible-gq").click(function () {
    b
      ? ($("#arrow-gq").rotate(0),
        $("#collapsible-gq").css("background-color", "#E0E0E0"))
      : ($("#arrow-gq").rotate(-90),
        $("#collapsible-gq").css("background-color", "#EEEEEE"));
    b = !b;
  });
}
function plugCollapsibleJQuery() {
  var a = [],
    c = parseInt($(".icon-close-open").css("height"));
  $(".expandable-panel-heading").click(function () {
    var b = $(this).next(),
      d = parseInt($(this).parent().attr("ID").substr(3, 2));
    parseInt(b.css("margin-top")) <= -1 * a[d]
      ? (b.clearQueue(),
        b.stop(),
        b
          .prev()
          .find(".icon-close-open")
          .css("background-position", "0px -" + c + "px"),
        b.animate({ "margin-top": 0 }, 500))
      : (b.clearQueue(),
        b.stop(),
        b.prev().find(".icon-close-open").css("background-position", "0px 0px"),
        b.animate({ "margin-top": -1 * a[d] }, 500));
  });
  (function () {
    for (var b = 1; 3 >= b; b++)
      (a[b] = parseInt(
        $("#cp-" + b)
          .find(".expandable-panel-content")
          .css("height"),
      )),
        $("#cp-" + b)
          .find(".expandable-panel-content")
          .css("margin-top", -a[b]),
        0 == b &&
          ($("#cp-" + b)
            .find(".icon-close-open")
            .css("background-position", "0px -" + c + "px"),
          $("#cp-" + b)
            .find(".expandable-panel-content")
            .css("margin-top", 0));
  })();
}
function exportSVG() {
  var a = document.querySelector("svg");
  a.setAttribute("width", "1100");
  a.setAttribute("style", "background: white;");
  var c = new XMLSerializer().serializeToString(a),
    b = document.createElement("canvas"),
    d = a.getBoundingClientRect();
  b.width = 3 * d.width;
  b.height = 3 * d.height;
  b.style.width = d.width;
  b.style.height = d.height;
  var f = b.getContext("2d");
  f.scale(3, 3);
  var e = document.createElement("img");
  e.setAttribute(
    "src",
    "data:image/svg+xml;base64," + btoa(unescape(encodeURIComponent(c))),
  );
  e.onload = function () {
    f.drawImage(e, 0, 0);
    var a = b.toDataURL("image/png", 1),
      c = '<img src="' + a + '">';
    d3.select("#pngdataurl").html(c);
    c = document.createElement("a");
    c.download = "download_img.png";
    c.href = a;
    document.body.appendChild(c);
    c.click();
  };
  a.setAttribute("width", "1000");
  a.setAttribute("style", "background: transparent;");
}
function drawColorGuides() {
  $(".legend-section").attr("transform", "translate(700, 700)");
}
