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
      qtarogenes_shoot_sibling: 27,
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
    brush: {},
  },
  allTraitData = {
    keys: ["name", "start", "length", "trackIndex"],
    annots: [
      { chr: "1", annots: [] },
      { chr: "2", annots: [] },
      { chr: "3", annots: [] },
      { chr: "4", annots: [] },
      { chr: "5", annots: [] },
      { chr: "6", annots: [] },
      { chr: "7", annots: [] },
      { chr: "8", annots: [] },
      { chr: "9", annots: [] },
      { chr: "10", annots: [] },
      { chr: "11", annots: [] },
      { chr: "12", annots: [] },
    ],
  },
  spinnerConfig = {
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
  },
  traitData = [],
  lfUrls = [],
  dropdownMenuForm =
    '<div><ul class="hover"><li class="hoverli"><ul class="file_menu"><li class="header-menu"><b class="white-text">Options</b></li><li><a id="brush0" class="show-jbrowse" onclick="redirectToJBrowse(this.id)">Show in JBrowse</a></li><li><a class="plot-genes" onclick="plotGeneAnnotation()">Plot all genes</a></li><hr id="divider"><li class="header-menu"><b class="white-text">Brush</b></li><li><a id="brush0" class="identify-the-brush" onclick="deleteThisBrush(this.id)">Delete this brush</a></li><li><a onclick="deleteAllBrush()">Delete all brush</a></li><hr id="divider"><li class="header-menu"><b class="white-text">Set base pair range</b></li><li><form class="white-text-default"><label for="StartBP">Start:</label><input type="number" name="StartBP" value="startBp" class="inline-textbox" id="startBPTextbox"></form></li><li><form class="white-text-default"><label for="EndBP">End:</label><input type="number" name="EndBP" value="stopBp" class="inline-textbox" id="endBPTextbox"></form></li><li id="range-details"><p class="white-text-smaller" id="chr-name-details"><b class="white-text-smaller" id="chr-name"></b>max:<b class="white-text-smaller" id="chr-name-max"></b><button type="button" id="brush0" class="submit-chr-details" onclick="setTheBrush(this.id)">Submit</button></p></li><li><p class="red-text" id="message-input-menu"></li></ul></li></ul></div>',
  defaultColor = [
    { id: "oryzabase_trait_genes", color: "#424242" },
    { id: "qtaro_trait_genes", color: "#F44336" },
    { id: "qtarogenes_bacterial_blight_resistance", color: "#9C27B0" },
    { id: "qtarogenes_blast_resistance", color: "#673AB7" },
    { id: "qtarogenes_cold_tolerance", color: "#3F51B5" },
    { id: "qtarogenes_culm_leaf", color: "#2196F3" },
    { id: "qtarogenes_drought_tolerance", color: "#00BCD4" },
    { id: "qtarogenes_dwarf", color: "#009688" },
    { id: "qtarogenes_eating_quality", color: "#8BC34A" },
    { id: "qtarogenes_flowering", color: "#69F0AE" },
    { id: "qtarogenes_germination_dormancy", color: "#FFC107" },
    { id: "qtarogenes_insect_resistance", color: "#FF9800" },
    { id: "qtarogenes_lethality", color: "#FF5722" },
    { id: "qtarogenes_lodging_resistance", color: "#795548" },
    { id: "qtarogenes_morphological_trait", color: "#9E9E9E" },
    { id: "qtarogenes_other_disease_resistance", color: "#607D8B" },
    { id: "qtarogenes_other_soil_stress_tolerance", color: "#B71C1C" },
    { id: "qtarogenes_other_stress_resistance", color: "#880E4F" },
    { id: "qtarogenes_others", color: "#4A148C" },
    { id: "qtarogenes_panicle_flower", color: "#311B92" },
    { id: "qtarogenes_physiological_trait", color: "#1A237E" },
    { id: "qtarogenes_resistance_or_tolerance", color: "#0D47A1" },
    { id: "qtarogenes_root", color: "#01579B" },
    { id: "qtarogenes_salinity_tolerance", color: "#006064" },
    { id: "qtarogenes_seed", color: "#004D40" },
    { id: "qtarogenes_sheath_blight_resistance", color: "#1B5E20" },
    { id: "qtarogenes_shoot_seedling", color: "#827717" },
    { id: "qtarogenes_source_activity", color: "#3E2723" },
    { id: "qtarogenes_sterility", color: "#212121" },
    { id: "qtarogenes_submergency_tolerance", color: "#BDBDBD" },
    { id: "qtaroqtl", color: "#F44337" },
    { id: "qtaroqtl_bacterial_blight_resistance", color: "#9C27B1" },
    { id: "qtaroqtl_blast_resistance", color: "#673AB8" },
    { id: "qtaroqtl_cold_tolerance", color: "#3F51B6" },
    { id: "qtaroqtl_culm_leaf", color: "#2196F4" },
    { id: "qtaroqtl_drought_tolerance", color: "#00BCD5" },
    { id: "qtaroqtl_dwarf", color: "#009687" },
    { id: "qtaroqtl_eating_quality", color: "#8BC34B" },
    { id: "qtaroqtl_flowering", color: "#69F0AF" },
    { id: "qtaroqtl_germination_dormancy", color: "#FFC108" },
    { id: "qtaroqtl_insect_resistance", color: "#FF9801" },
    { id: "qtaroqtl_lethality", color: "#FF5723" },
    { id: "qtaroqtl_lodging_resistance", color: "#795549" },
    { id: "qtaroqtl_morphological_trait", color: "#9E9E9F" },
    { id: "qtaroqtl_other_disease_resistance", color: "#607D8C" },
    { id: "qtaroqtl_other_soil_stress_tolerance", color: "#B71C1D" },
    { id: "qtaroqtl_other_stress_resistance", color: "#880E4E" },
    { id: "qtaroqtl_others", color: "#4A148D" },
    { id: "qtaroqtl_panicle_flower", color: "#311B93" },
    { id: "qtaroqtl_physiological_trait", color: "#1A237F" },
    { id: "qtaroqtl_resistance_or_tolerance", color: "#0D47A2" },
    { id: "qtaroqtl_root", color: "#01579D" },
    { id: "qtaroqtl_salinity_tolerance", color: "#006065" },
    { id: "qtaroqtl_seed", color: "#004D41" },
    { id: "qtaroqtl_sheath_blight_resistance", color: "#1B5E21" },
    { id: "qtaroqtl_shoot_seedling", color: "#827718" },
    { id: "qtaroqtl_source_activity", color: "#3E2722" },
    { id: "qtaroqtl_sterility", color: "#212122" },
    { id: "qtaroqtl_submergency_tolerance", color: "#BDBDBE" },
  ],
  protanopiaNoRed = [
    { id: "oryzabase_trait_genes", color: "#000E1F" },
    { id: "qtaro_trait_genes", color: "#001D3E" },
    { id: "qtarogenes_bacterial_blight_resistance", color: "#002C5D" },
    { id: "qtarogenes_blast_resistance", color: "#1D366A" },
    { id: "qtarogenes_cold_tolerance", color: "#003F85" },
    { id: "qtarogenes_culm_leaf", color: "#003B7C" },
    { id: "qtarogenes_drought_tolerance", color: "#004A9C" },
    { id: "qtarogenes_dwarf", color: "#004A9B" },
    { id: "qtarogenes_eating_quality", color: "#0056B4" },
    { id: "qtarogenes_flowering", color: "#0075F8" },
    { id: "qtarogenes_germination_dormancy", color: "#3A6DD4" },
    { id: "qtarogenes_insect_resistance", color: "#7FA0FF" },
    { id: "qtarogenes_lethality", color: "#5A699B" },
    { id: "qtarogenes_lodging_resistance", color: "#8F9BCD" },
    { id: "qtarogenes_morphological_trait", color: "#C4CEFF" },
    { id: "qtarogenes_other_disease_resistance", color: "#312B00" },
    { id: "qtarogenes_other_soil_stress_tolerance", color: "#625700" },
    { id: "qtarogenes_other_stress_resistance", color: "#635A2F" },
    { id: "qtarogenes_others", color: "#94852D" },
    { id: "qtarogenes_panicle_flower", color: "#938200" },
    { id: "qtarogenes_physiological_trait", color: "#C4AE00" },
    { id: "qtarogenes_resistance_or_tolerance", color: "#F5DA00" },
    { id: "qtarogenes_root", color: "#F6DB29" },
    { id: "qtarogenes_salinity_tolerance", color: "#C5B02B" },
    { id: "qtarogenes_seed", color: "#F8DF5C" },
    { id: "qtarogenes_sheath_blight_resistance", color: "#FBE68F" },
    { id: "qtarogenes_shoot_seedling", color: "#C7B55E" },
    { id: "qtarogenes_source_activity", color: "#978C60" },
    { id: "qtarogenes_sterility", color: "#CBBE92" },
    { id: "qtarogenes_submergency_tolerance", color: "#FFF0C6" },
    { id: "qtaroqtl", color: "#001D3F" },
    { id: "qtaroqtl_bacterial_blight_resistance", color: "#002C5E" },
    { id: "qtaroqtl_blast_resistance", color: "#1D366B" },
    { id: "qtaroqtl_cold_tolerance", color: "#003F86" },
    { id: "qtaroqtl_culm_leaf", color: "#003B7D" },
    { id: "qtaroqtl_drought_tolerance", color: "#004A9D" },
    { id: "qtaroqtl_dwarf", color: "#004A9F" },
    { id: "qtaroqtl_eating_quality", color: "#0056B%" },
    { id: "qtaroqtl_flowering", color: "#0075F7" },
    { id: "qtaroqtl_germination_dormancy", color: "#3A6DD5" },
    { id: "qtaroqtl_insect_resistance", color: "#7FA0FE" },
    { id: "qtaroqtl_lethality", color: "#5A699C" },
    { id: "qtaroqtl_lodging_resistance", color: "#8F9BCE" },
    { id: "qtaroqtl_morphological_trait", color: "#C4CEFE" },
    { id: "qtaroqtl_other_disease_resistance", color: "#312B01" },
    { id: "qtaroqtl_other_soil_stress_tolerance", color: "#625701" },
    { id: "qtaroqtl_other_stress_resistance", color: "#635A2E" },
    { id: "qtaroqtl_others", color: "#94852E" },
    { id: "qtaroqtl_panicle_flower", color: "#938201" },
    { id: "qtaroqtl_physiological_trait", color: "#C4AE01" },
    { id: "qtaroqtl_resistance_or_tolerance", color: "#F5DA01" },
    { id: "qtaroqtl_root", color: "#F6DB28" },
    { id: "qtaroqtl_salinity_tolerance", color: "#C5B02C" },
    { id: "qtaroqtl_seed", color: "#F8DF5D" },
    { id: "qtaroqtl_sheath_blight_resistance", color: "#FBE68D" },
    { id: "qtaroqtl_shoot_seedling", color: "#C7B55F" },
    { id: "qtaroqtl_source_activity", color: "#978C61" },
    { id: "qtaroqtl_sterility", color: "#CBBE93" },
    { id: "qtaroqtl_submergency_tolerance", color: "#FFF0C7" },
  ],
  deutanopiaNoGreen = [
    { id: "oryzabase_trait_genes", color: "#000E1F" },
    { id: "qtaro_trait_genes", color: "#001D3E" },
    { id: "qtarogenes_bacterial_blight_resistance", color: "#002C5D" },
    { id: "qtarogenes_blast_resistance", color: "#1D366A" },
    { id: "qtarogenes_cold_tolerance", color: "#003F85" },
    { id: "qtarogenes_culm_leaf", color: "#003B7C" },
    { id: "qtarogenes_drought_tolerance", color: "#004A9C" },
    { id: "qtarogenes_dwarf", color: "#004A9B" },
    { id: "qtarogenes_eating_quality", color: "#0056B4" },
    { id: "qtarogenes_flowering", color: "#0075F8" },
    { id: "qtarogenes_germination_dormancy", color: "#3A6DD4" },
    { id: "qtarogenes_insect_resistance", color: "#7FA0FF" },
    { id: "qtarogenes_lethality", color: "#5A699B" },
    { id: "qtarogenes_lodging_resistance", color: "#8F9BCD" },
    { id: "qtarogenes_morphological_trait", color: "#C4CEFF" },
    { id: "qtarogenes_other_disease_resistance", color: "#36290B" },
    { id: "qtarogenes_other_soil_stress_tolerance", color: "#6D5636" },
    { id: "qtarogenes_other_stress_resistance", color: "#6D5216" },
    { id: "qtarogenes_others", color: "#A47E3B" },
    { id: "qtarogenes_panicle_flower", color: "#A47B21" },
    { id: "qtarogenes_physiological_trait", color: "#DAA52C" },
    { id: "qtarogenes_resistance_or_tolerance", color: "#DBA741" },
    { id: "qtarogenes_root", color: "#FFD28E" },
    { id: "qtarogenes_salinity_tolerance", color: "#FFD495" },
    { id: "qtarogenes_seed", color: "#FFD8A8" },
    { id: "qtarogenes_sheath_blight_resistance", color: "#DBAC6D" },
    { id: "qtarogenes_shoot_seedling", color: "#A5866A" },
    { id: "qtarogenes_source_activity", color: "#DCB79D" },
    { id: "qtarogenes_sterility", color: "#FFE1C5" },
    { id: "qtarogenes_submergency_tolerance", color: "#FFEEE5" },
    { id: "qtaroqtl", color: "#001D3F" },
    { id: "qtaroqtl_bacterial_blight_resistance", color: "#002C5E" },
    { id: "qtaroqtl_blast_resistance", color: "#1D366B" },
    { id: "qtaroqtl_cold_tolerance", color: "#003F86" },
    { id: "qtaroqtl_culm_leaf", color: "#003B7D" },
    { id: "qtaroqtl_drought_tolerance", color: "#004A9D" },
    { id: "qtaroqtl_dwarf", color: "#004A9F" },
    { id: "qtaroqtl_eating_quality", color: "#0056B%" },
    { id: "qtaroqtl_flowering", color: "#0075F7" },
    { id: "qtaroqtl_germination_dormancy", color: "#3A6DD5" },
    { id: "qtaroqtl_insect_resistance", color: "#7FA0FE" },
    { id: "qtaroqtl_lethality", color: "#5A699C" },
    { id: "qtaroqtl_lodging_resistance", color: "#8F9BCE" },
    { id: "qtaroqtl_morphological_trait", color: "#C4CEFE" },
    { id: "qtaroqtl_other_disease_resistance", color: "#36290C" },
    { id: "qtaroqtl_other_soil_stress_tolerance", color: "#6D5637" },
    { id: "qtaroqtl_other_stress_resistance", color: "#6D5217" },
    { id: "qtaroqtl_others", color: "#A47E3C" },
    { id: "qtaroqtl_panicle_flower", color: "#A47B22" },
    { id: "qtaroqtl_physiological_trait", color: "#DAA52D" },
    { id: "qtaroqtl_resistance_or_tolerance", color: "#DBA742" },
    { id: "qtaroqtl_root", color: "#FFD28F" },
    { id: "qtaroqtl_salinity_tolerance", color: "#FFD496" },
    { id: "qtaroqtl_seed", color: "#FFD8A9" },
    { id: "qtaroqtl_sheath_blight_resistance", color: "#DBAC6E" },
    { id: "qtaroqtl_shoot_seedling", color: "#A5866B" },
    { id: "qtaroqtl_source_activity", color: "#DCB79C" },
    { id: "qtaroqtl_sterility", color: "#FFE1C5" },
    { id: "qtaroqtl_submergency_tolerance", color: "#FFEEE6" },
  ],
  tritanopiaNoBlue = [
    { id: "oryzabase_trait_genes", color: "#152F33" },
    { id: "qtaro_trait_genes", color: "#2A5E66" },
    { id: "qtarogenes_bacterial_blight_resistance", color: "#408E99" },
    { id: "qtarogenes_blast_resistance", color: "#55BDCC" },
    { id: "qtarogenes_cold_tolerance", color: "#71ECFF" },
    { id: "qtarogenes_culm_leaf", color: "#3E6067" },
    { id: "qtarogenes_drought_tolerance", color: "#4D8F9A" },
    { id: "qtarogenes_dwarf", color: "#5FBECD" },
    { id: "qtarogenes_eating_quality", color: "#7AECFF" },
    { id: "qtarogenes_flowering", color: "#70929D" },
    { id: "qtarogenes_germination_dormancy", color: "#7CC0CF" },
    { id: "qtarogenes_insect_resistance", color: "#96EDFF" },
    { id: "qtarogenes_lethality", color: "#A3C4D3" },
    { id: "qtarogenes_lodging_resistance", color: "#BCEFFF" },
    { id: "qtarogenes_morphological_trait", color: "#87A8B6" },
    { id: "qtarogenes_other_disease_resistance", color: "#FD1700" },
    { id: "qtarogenes_other_soil_stress_tolerance", color: "#CA1200" },
    { id: "qtarogenes_other_stress_resistance", color: "#FF3332" },
    { id: "qtarogenes_others", color: "#970E00" },
    { id: "qtarogenes_panicle_flower", color: "#CC3234" },
    { id: "qtarogenes_physiological_trait", color: "#FF656B" },
    { id: "qtarogenes_resistance_or_tolerance", color: "#650900" },
    { id: "qtarogenes_root", color: "#993235" },
    { id: "qtarogenes_salinity_tolerance", color: "#CC656C" },
    { id: "qtarogenes_seed", color: "#FF97A2" },
    { id: "qtarogenes_sheath_blight_resistance", color: "#320400" },
    { id: "qtarogenes_shoot_seedling", color: "#663236" },
    { id: "qtarogenes_source_activity", color: "#99646C" },
    { id: "qtarogenes_sterility", color: "#CD97A3" },
    { id: "qtarogenes_submergency_tolerance", color: "#FFCAD9" },
    { id: "qtaroqtl", color: "#2A5E67" },
    { id: "qtaroqtl_bacterial_blight_resistance", color: "#408E98" },
    { id: "qtaroqtl_blast_resistance", color: "#55BDCD" },
    { id: "qtaroqtl_cold_tolerance", color: "#71ECFE" },
    { id: "qtaroqtl_culm_leaf", color: "#3E6068" },
    { id: "qtaroqtl_drought_tolerance", color: "#4D8F9B" },
    { id: "qtaroqtl_dwarf", color: "#5FBECE" },
    { id: "qtaroqtl_eating_quality", color: "#7AECFE" },
    { id: "qtaroqtl_flowering", color: "#70929E" },
    { id: "qtaroqtl_germination_dormancy", color: "#7CC0CE" },
    { id: "qtaroqtl_insect_resistance", color: "#96EDFE" },
    { id: "qtaroqtl_lethality", color: "#A3C4D4" },
    { id: "qtaroqtl_lodging_resistance", color: "#BCEFFE" },
    { id: "qtaroqtl_morphological_trait", color: "#87A8B7" },
    { id: "qtaroqtl_other_disease_resistance", color: "#FD1701" },
    { id: "qtaroqtl_other_soil_stress_tolerance", color: "#CA1201" },
    { id: "qtaroqtl_other_stress_resistance", color: "#FF3333" },
    { id: "qtaroqtl_others", color: "#970E01" },
    { id: "qtaroqtl_panicle_flower", color: "#CC3235" },
    { id: "qtaroqtl_physiological_trait", color: "#FF656C" },
    { id: "qtaroqtl_resistance_or_tolerance", color: "#650901" },
    { id: "qtaroqtl_root", color: "#993236" },
    { id: "qtaroqtl_salinity_tolerance", color: "#CC656D" },
    { id: "qtaroqtl_seed", color: "#FF97A3" },
    { id: "qtaroqtl_sheath_blight_resistance", color: "#320401" },
    { id: "qtaroqtl_shoot_seedling", color: "#663237" },
    { id: "qtaroqtl_source_activity", color: "#99646D" },
    { id: "qtaroqtl_sterility", color: "#CD97A2" },
    { id: "qtaroqtl_submergency_tolerance", color: "#FFCAD8" },
  ],
  brushAnnots = [],
  processedAnnotsObj = {},
  renderForm = function (b, a) {
    $.getJSON(b, function (a) {
      var b = [],
        d = "<" + a.html.type + "/>";
      b.push(
        "<" +
          a.html.html[0].type +
          ">" +
          a.html.html[0].html +
          "</" +
          a.html.html[0].type +
          ">",
      );
      for (var e = 1; e < a.html.html.length; e++) {
        var g = "<" + a.html.html[e].type + ">",
          k = "</" + a.html.html[e].type + ">",
          h = "<input",
          l = "",
          n = "",
          m;
        "traitGenes" === a.html.id
          ? (m =
              '<div class="color-block" id="color-block-' +
              (e - 1) +
              '"></div>')
          : "qtl" === a.html.id &&
            (m =
              '<div class="color-block" id="color-block-' +
              (e - 1 + 30) +
              '"></div>');
        $.each(a.html.html[e].html, function (a, b) {
          "id" == a && (n = b);
          "caption" == a
            ? (l = "<label for='" + n + "'>" + m + b + "</label>")
            : (h += " " + a + "='" + b + "'");
        });
        h += "></input>";
        item = g + h + l + k;
        b.push(item);
      }
      "traitGenes" === a.html.id
        ? $(d, { id: a.html.id, html: b.join("") }).appendTo("#form-render")
        : "qtl" === a.html.id &&
          $(d, { id: a.html.id, html: b.join("") }).appendTo(
            "#form-render-qtl",
          );
    })
      .done(function () {
        console.log("Form rendered");
      })
      .fail(function () {
        console.warn("Error form render");
      });
  };
function fillColorBlock() {
  var b = 0,
    a;
  $.each(ideogram.config.annotationTracks, function (d, c) {
    a = c.color;
    traitGeneID = "#color-block-" + b;
    qtlID = "#color-block-" + (b + 30);
    $(traitGeneID).css("background-color", a);
    $(qtlID).css("background-color", a);
    b += 1;
  });
}
var renderCollapsible = function (b) {
  $.getJSON(b, function (a) {
    for (var b in a) {
      for (
        var c = a[b], f = [], e = $("<ul/>"), g = 0;
        g < c.collapsible_content.length;
        g++
      ) {
        var k = "<" + c.collapsible_content[g].tag + " class='hoverable-li'>",
          h = "</" + c.collapsible_content[g].tag + ">",
          l = "<input",
          n = "",
          m = "",
          p;
        "traitGenes" === b
          ? (p = '<div class="color-block" id="color-block-' + g + '"></div>')
          : "qtl" === b &&
            (p =
              '<div class="color-block" id="color-block-' +
              (g + 30) +
              '"></div>');
        $.each(c.collapsible_content[g].html, function (a, b) {
          "id" == a && (m = b);
          "caption" == a
            ? (n = "<label for='" + m + "'>" + p + b + "</label>")
            : (l += " " + a + "='" + b + "'");
        });
        l += "></input>";
        item = k + l + n + h;
        f.push(item);
      }
      $(f.join("")).appendTo(e);
      $(e).appendTo("#" + c.id);
      $("#" + c.appendToClass).text(" " + c.header);
    }
  })
    .done(function () {
      console.log("Form rendered");
      fillColorBlock();
    })
    .fail(function () {
      console.warn("Error form render");
    });
};
function toggleSpinner(b, a) {
  a || b.stop();
}
function getJsonData(b, a) {
  d3.json(b, function (d, c) {
    d ? a(null) : a(c, b);
  });
}
var asyncLoop = function (b) {
  var a = 0,
    d = b.length,
    c = function () {
      a == d ? b.callback() : (a++, b.functionToLoop(c, a));
    };
  c();
};
function reformatTraitData(b) {
  var a = /brush/;
  var d = /search/;
  var c = /(qtl|QTL)/.test(b)
    ? "qtl"
    : a.test(b) || d.test(b)
      ? "brush"
      : "traitGenes";
  for (a = 0; a < traitData.length; a++) {
    var f = traitData[a],
      e = [];
    if (f.data.length) {
      var g = f.chrNum;
      for (d = 0; d < f.data.length; d++) {
        var k = f.data,
          h = Array(4);
        h[0] = k[d][7];
        h[1] = k[d][1];
        h[2] = k[d][2] - k[d][1];
        h[3] = filterMap[c][b];
        e.push(h);
      }
      allTraitData.annots[g - 1].annots.push.apply(
        allTraitData.annots[g - 1].annots,
        e,
      );
    }
  }
  return allTraitData;
}
var isURLExisting = [];
function getTrackDataUrls(b) {
  var a,
    d = [];
  for (a = 1; 13 > a; a++) {
    var c =
        "https://snpseekv3.irri-e-extension.com/jbrowse/data/tracks/" +
        b +
        "/chr",
      c = 9 < a ? c + a.toString() : c + "0" + a.toString();
    var f = window.XMLHttpRequest
      ? new XMLHttpRequest()
      : new ActiveXObject("Microsoft.XMLHTTP");
    f.open("GET", c + "/trackData.json", !1);
    f.send();
    404 !== f.status ? d.push(c + "/trackData.json") : d.push("");
  }
  return d;
}
function populateLfUrls(b, a, d) {
  var c;
  for (c = 1; c <= a; c++) {
    var f = b + "/lf-" + c.toString() + ".json";
    lfUrls.push([f, d]);
  }
}
function getTrackData(b, a) {
  var d = document.getElementById("chromosome-render"),
    c = new Spinner(spinnerConfig).spin(d),
    f = [],
    e,
    g;
  toggleSpinner(c, !0);
  asyncLoop({
    length: 13,
    functionToLoop: function (b, c) {
      setTimeout(function () {
        getJsonData(a[c - 1], function (a, b) {
          if (a.featureCount) {
            var d = a.intervals.nclist,
              f = d.length,
              e = {};
            4 == d[0].length
              ? ((d = b.replace("/trackData.json", "")),
                populateLfUrls(d, f, c))
              : ((e.chrNum = c), (e.data = d), traitData.push(e));
          }
        });
        b();
      }, 100);
    },
    callback: function () {
      if (lfUrls.length)
        for (g = 0; 12 > g; g++) {
          var a = {};
          for (e = 0; e < lfUrls.length; e++)
            lfUrls[e][1] == g + 1 &&
              $.ajax({
                dataType: "json",
                async: !1,
                url: lfUrls[e][0],
                data: void 0,
                success: function (a) {
                  f.push.apply(f, a);
                },
              });
          a.chrNum = g + 1;
          a.data = f;
          traitData.push(a);
          f = [];
        }
      lfUrls = [];
      config.rawAnnots = reformatTraitData(b);
      config.selectedTrack = b;
      config.allTracks = allTracks;
      toggleSpinner(c, !1);
      ideogram = new Ideogram(config);
      setUpBrush();
      setUpZoomButtons();
      return ideogram;
    },
  });
}
var brushTrackCount = 59;
function removeSelectedTrack(b) {
  b = filterMap.traitGenes[b];
  var a, d;
  for (a = 0; 12 > a; a++) {
    var c = allTraitData.annots[a].annots.length;
    for (d = 0; d < c; d++)
      allTraitData.annots[a].annots[d][3] === b &&
        (allTraitData.annots[a].annots.splice(d, 1), d--, c--);
  }
  return allTraitData;
}
function removeSelectedTrack(b) {
  var a = /brush/,
    d = /search/,
    a = /(qtl|QTL)/.test(b)
      ? "qtl"
      : a.test(b) || d.test(b)
        ? "brush"
        : "traitGenes",
    d = filterMap[a][b];
  for (b = 0; 12 > b; b++)
    for (var c = allTraitData.annots[b].annots.length, a = 0; a < c; a++)
      allTraitData.annots[b].annots[a][3] === d &&
        (allTraitData.annots[b].annots.splice(a, 1), a--, c--);
  return allTraitData;
}
function getAllTraitData() {
  return allTraitData;
}
var allTracks = [],
  allTracksCount = 0;
function addTrack(b) {
  var a = /brush/,
    d = /search/;
  jQuery.isEmptyObject(filterMap.brush) && (filterMap.brush = {});
  /(qtl|QTL)/.test(b)
    ? ((a = filterMap.qtl[b]), console.log("qtl | " + a))
    : a.test(b) || d.test(b)
      ? (filterMap.brush.hasOwnProperty(b) || (brushTrackCount += 1),
        (filterMap.brush[b] = brushTrackCount),
        (a = filterMap.brush[b]))
      : (a = filterMap.traitGenes[b]);
  allTracks.push({ track: b, trackIndex: allTracksCount, mapping: a });
  allTracksCount++;
}
function removeTrack(b) {
  for (var a, d = 0, c = 0; c < allTracks.length; c++)
    if (((a = allTracks[c]), a.track === b)) {
      allTracks.splice(c, 1);
      allTracksCount--;
      d = c;
      break;
    }
  for (c = d; c < allTracks.length; c++)
    (a = allTracks[c].trackIndex), (allTracks[c].trackIndex = a - 1);
}
function getAllTracksCount() {
  return allTracksCount;
}
function displayLinearScale() {
  var b = [50, 50, 70, 100],
    a = d3.scale.linear().domain([0, 43270923]).range([0, 800]);
  d3.svg
    .line()
    .x(function (a, b) {
      return x(b);
    })
    .y(function (b) {
      return a(b);
    });
  var b = d3
      .select("#ideogram")
      .append("svg:svg")
      .attr("id", "linear-scale")
      .attr("width", 10 + b[1] + b[3] + 950)
      .attr("height", 800 + b[0] + b[2])
      .append("svg:g")
      .attr("transform", "translate(950,30)"),
    d = d3.svg.axis().scale(a).ticks(7).orient("right"),
    b = b
      .append("svg:g")
      .attr("class", "y axis")
      .attr("transform", "translate(0,0)")
      .call(d);
}
