function getViewType(a) {
  "Histogram" === a
    ? (config.annotationsLayout = "histogram")
    : "Tracks" === a && (config.annotationsLayout = "tracks");
  toggleLinearScale("show");
  d3.select("svg").remove();
  ideogram = new Ideogram(config);
  setUpBrush();
}
function toggleFilter(a) {
  console.log(allTracks);
  var e = a.id,
    f = !1,
    g = !1,
    b = $("#" + e).attr("tracks"),
    d = parseInt(a.id.replace(/[^0-9\.]/g, ""), 10);
  if (-1 !== b.indexOf("brush")) {
    var c = arrayOfColorsBrushes[d - 59];
    f = !0;
  } else
    -1 !== b.indexOf("search")
      ? ((c = arrayOfColorsBrushes[d - 59]), (g = !0))
      : isTracksOn &&
        (null != a.id.match("traitGenes")
          ? (c = colorSettings[filterMap.traitGenes[b] - 1].color)
          : null != a.id.match("qtl") &&
            (c = colorSettings[filterMap.qtl[b]].color));
  $("#" + e).is(":checked")
    ? (addTrack(b),
      f
        ? configureBrushAnnot(d - 59, c, !1, a.id)
        : g && configureSearchAnnot(d - 59, c, !1, a.id),
      (a = getTrackDataUrls(b)),
      (traitData = []),
      d3.select("#ideogram").remove(),
      (ideogram = getTrackData(b, a, config)),
      d3.selectAll("path[fill = '" + c + "']").attr("visibility", "show"))
    : (removeTrack(b),
      (b = removeSelectedTrack(b)),
      (config.rawAnnots = b),
      d3.select("#ideogram").remove(),
      (ideogram = new Ideogram(config)),
      d3.selectAll("path[fill = '" + c + "']").attr("visibility", "hidden"));
  0 < getAllTracksCount() && $("#jb-div").show();
}
function colorBlindMode(a) {
  config.annotationTracks =
    "proto" === a
      ? protanopiaNoRed
      : "deuto" === a
        ? deutanopiaNoGreen
        : "trito" === a
          ? tritanopiaNoBlue
          : defaultColor;
  toggleLinearScale("show");
  d3.select("svg").remove();
  ideogram = new Ideogram(config);
  adjustIdeogramSVG();
  dropdownMenuSetup();
  fillColorBlock();
}
document.getElementById("defaultOpen").click();
document.getElementById("goToInstr").click();
var filterMap = {
    traitGenes: {
      oryzabase_trait_genes: 1,
      qtaro_trait_genes: 2,
      qtarogenes_bacterial_blight_resistance: 3,
      qtarogenes_blast_resistance: 4,
      qtarogenes_cold_tolerance: 5,
      qtarogenes_culm_leaf: 6,
      qtarogenes_drought_tolerance: 7,
      qtarogenes_dwarf: 8,
      qtarogenes_eating_quality: 9,
      qtarogenes_flowering: 10,
      qtarogenes_germination_dormancy: 11,
      qtarogenes_insect_resistance: 12,
      qtarogenes_lethality: 13,
      qtarogenes_lodging_resistance: 14,
      qtarogenes_morphological_trait: 15,
      qtarogenes_other_disease_resistance: 16,
      qtarogenes_other_soil_stress_tolerance: 17,
      qtarogenes_other_stress_resistance: 18,
      qtarogenes_others: 19,
      qtarogenes_panicle_flower: 20,
      qtarogenes_physiological_trait: 21,
      qtarogenes_resistance_or_tolerance: 22,
      qtarogenes_root: 23,
      qtarogenes_salinity_tolerance: 24,
      qtarogenes_seed: 25,
      qtarogenes_sheath_blight_resistance: 26,
      qtarogenes_shoot_seedling: 27,
      qtarogenes_source_activity: 28,
      qtarogenes_sterility: 29,
      qtarogenes_submergency_tolerance: 30,
    },
    qtl: {
      "QTARO QTL": 31,
      qtaroqtl_bacterial_blight_resistance: 32,
      qtaroqtl_blast_resistance: 33,
      qtaroqtl_cold_tolerance: 34,
      qtaroqtl_culm_leaf: 35,
      qtaroqtl_drought_tolerance: 36,
      qtaroqtl_dwarf: 37,
      qtaroqtl_eating_quality: 38,
      qtaroqtl_flowering: 39,
      qtaroqtl_germination_dormancy: 40,
      qtaroqtl_insect_resistance: 41,
      qtaroqtl_lethality: 42,
      qtaroqtl_lodging_resistance: 43,
      qtaroqtl_morphological_trait: 44,
      qtaroqtl_other_disease_resistance: 45,
      qtaroqtl_other_soil_stress_tolerance: 46,
      qtaroqtl_other_stress_resistance: 47,
      qtaroqtl_others: 48,
      qtaroqtl_panicle_flower: 49,
      qtaroqtl_physiological_trait: 50,
      qtaroqtl_resistance_or_tolerance: 51,
      qtaroqtl_root: 52,
      qtaroqtl_salinity_tolerance: 53,
      qtaroqtl_seed: 54,
      qtaroqtl_sheath_blight_resistance: 55,
      qtaroqtl_shoot_seedling: 56,
      qtaroqtl_source_activity: 57,
      qtaroqtl_sterility: 58,
      qtaroqtl_submergency_tolerance: 59,
    },
  },
  colorSettings = defaultColor,
  w = 0.01 * $(window).width(),
  h = 0.9 * $(window).height();
function writeSelectedRange() {}
var isTracksOn = !1,
  config = {
    organism: "rice",
    barWidth: 4,
    chrWidth: 18,
    chrHeight: 800,
    chrMargin: 30,
    annotationHeight: 4,
    annotationTracks: colorSettings,
    annotationsLayout: "tracks",
    container: "#chromosome-render",
    brush: !0,
    onBrushMove: writeSelectedRange,
    onLoad: writeSelectedRange,
  },
  ideogram;
getViewType("Tracks");
$("#view-type_tracks").attr("checked", !0);
renderCollapsible("/rice-ideogram/data/filter/dataSet.json");
plugCollapsibleJQuery();
fillColorBlock();
$("#jbrowse").hide();
