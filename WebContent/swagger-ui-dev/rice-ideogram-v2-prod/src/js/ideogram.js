// Developed by Eric Weitz (https://github.com/eweitz)
// Modifications for rice organism by Leensey Lawas [*] (https://github.com/lmlawas1)
// Further modifications for the extension of Ms. Lawas' project by Jourish Abasolo (https://github.com/jjdcabasolo)

var Ideogram = function (c) {
    this.config = JSON.parse(JSON.stringify(c));
    this.debug = !1;
    this.config.bandDir || (this.config.bandDir = "/rice-ideogram/data");
    this.config.container || (this.config.container = "body");
    this.config.resolution || (this.config.resolution = 850);
    !1 === "showChromosomeLabels" in this.config &&
      (this.config.showChromosomeLabels = !0);
    this.config.chrMargin || (this.config.chrMargin = 10);
    if (!this.config.orientation) {
      var e = "vertical";
      this.config.orientation = e;
    }
    if (!this.config.chrHeight) {
      var f = this.config.container,
        d = document.querySelector(f).getBoundingClientRect();
      e = "vertical" === e ? d.height : d.width;
      "body" == f && (e = 500);
      this.config.chrHeight = e;
    }
    this.config.chrWidth ||
      ((f = 10),
      (e = this.config.chrHeight),
      900 > e && 500 < e
        ? (f = Math.round(e / 40))
        : 900 <= e && (f = Math.round(e / 45)),
      (this.config.chrWidth = f));
    this.config.showBandLabels || (this.config.showBandLabels = !1);
    this.config.brush || (this.config.brush = !1);
    this.config.rows || (this.config.rows = 1);
    this.bump = Math.round(this.config.chrHeight / 125);
    this.adjustedBump = !1;
    200 > this.config.chrHeight && ((this.adjustedBump = !0), (this.bump = 4));
    c.showBandLabels && (this.config.chrMargin += 20);
    c.chromosome &&
      ((this.config.chromosomes = [c.chromosome]),
      !1 === "showBandLabels" in c && (this.config.showBandLabels = !0),
      !1 === "rotatable" in c && (this.config.rotatable = !1));
    this.initAnnotSettings();
    this.config.chrMargin =
      this.config.chrMargin +
      this.config.chrWidth +
      2 * this.config.annotTracksHeight;
    c.onLoad && (this.onLoadCallback = c.onLoad);
    c.onDrawAnnots && (this.onDrawAnnotsCallback = c.onDrawAnnots);
    c.onBrushMove && (this.onBrushMoveCallback = c.onBrushMove);
    this.coordinateSystem = "iscn";
    this.maxLength = { bp: 0, iscn: 0 };
    this.organisms = {
      9606: {
        commonName: "Human",
        scientificName: "Homo sapiens",
        scientificNameAbbr: "H. sapiens",
        assemblies: {
          default: "GCF_000001305.14",
          GRCh38: "GCF_000001305.14",
          GRCh37: "GCF_000001305.13",
        },
      },
      10090: {
        commonName: "Mouse",
        scientificName: "Mus musculus",
        scientificNameAbbr: "M. musculus",
        assemblies: { default: "GCF_000000055.19" },
      },
      7227: {
        commonName: "Fly",
        scientificName: "Drosophlia melanogaster",
        scientificNameAbbr: "D. melanogaster",
      },
      4530: {
        commonName: "Rice",
        scientificName: "Oryza sativa",
        scientificNameAbbr: "O. sativa",
      },
    };
    this.chromosomesArray = [];
    this.bandsToShow = [];
    this.chromosomes = {};
    this.numChromosomes = 0;
    this.bandData = {};
    this.init();
  },
  linearScaleVisibility = !0;
function toggleLinearScale(c) {
  linearScaleVisibility = !0;
  d3.select("#linear-scale").attr("visibility", "visible");
}
Ideogram.prototype.getBands = function (c, e, f) {
  var d = {},
    a;
  "chrBands" === c.slice(0, 8) && (a = "native");
  if ("undefined" === typeof chrBands && "native" !== a) {
    var h = /\t/;
    c = c.split(/\r\n|\n/);
    var b = 1;
  } else (h = / /), (c = "native" === a ? eval(c) : c), (b = 0);
  var k = c[0].split(h)[0];
  a = "#chromosome" == k ? "ncbi" : "#chrom" == k ? "ucsc" : "native";
  k = c.length;
  if ("ncbi" === a || "native" === a)
    for (; b < k; b++) {
      a = c[b].split(h);
      var l = a[0];
      if ("undefined" === typeof f || -1 !== f.indexOf(l)) {
        !1 === l in d && (d[l] = []);
        var g = a[7];
        a[8] && (g += a[8]);
        a = {
          chr: l,
          bp: { start: parseInt(a[5], 10), stop: parseInt(a[6], 10) },
          iscn: { start: parseInt(a[3], 10), stop: parseInt(a[4], 10) },
          px: { start: -1, stop: -1, width: -1 },
          name: a[1] + a[2],
          stain: g,
          taxid: e,
        };
        d[l].push(a);
      }
    }
  else if ("ucsc" === a)
    for (; b < k; b++)
      if (((a = c[b].split(h)), a[0] === "chr" + chromosomeName)) {
        g = a[4];
        "n/a" === g && (g = "gpos100");
        f = parseInt(a[1], 10);
        var m = parseInt(a[2], 10);
        a = {
          chr: a[0].split("chr")[1],
          bp: { start: f, stop: m },
          iscn: { start: f, stop: m },
          px: { start: -1, stop: -1, width: -1 },
          name: a[3],
          stain: g,
          taxid: e,
        };
        d[l].push(a);
      }
  return d;
};
Ideogram.prototype.colorArms = function (c, e) {
  var f = this;
  f.chromosomesArray.forEach(function (d, a) {
    var h = d.bands,
      b = h[d.pcenIndex + 1],
      k = d.id,
      l = f.config.chrMargin * (a + 1),
      g = f.config.chrWidth;
    pcenStart = h[d.pcenIndex].px.start;
    qcenStop = b.px.stop;
    d3.select("#" + k)
      .append("line")
      .attr("x1", pcenStart)
      .attr("y1", l + 0.2)
      .attr("x2", pcenStart)
      .attr("y2", l + g - 0.2)
      .style("stroke", c);
    d3.select("#" + k)
      .append("line")
      .attr("x1", qcenStop)
      .attr("y1", l + 0.2)
      .attr("x2", qcenStop)
      .attr("y2", l + g - 0.2)
      .style("stroke", e);
    d3.selectAll("#" + k + " .band")
      .data(d.bands)
      .style("fill", function (a, b) {
        return b <= d.pcenIndex ? c : e;
      });
  });
  d3.selectAll(".p-ter.chromosomeBorder").style("fill", c);
  d3.selectAll(".q-ter.chromosomeBorder").style("fill", e);
};
Ideogram.prototype.getChromosomeModel = function (c, e, f, d) {
  var a = {},
    h = this.config.chrHeight,
    b = this.maxLength;
  var k = this.coordinateSystem;
  a.chrIndex = d;
  a.name = e;
  !0 === this.config.fullChromosomeLabels &&
    (a.name = this.organisms[f].scientificNameAbbr + " chr" + a.name);
  a.id = "chr" + e + "-" + f;
  a.length = c[c.length - 1][k].stop;
  var l = a.length;
  for (var g = (d = 0); g < c.length; g++)
    (e = c[g]),
      (f = (((h * a.length) / b[k]) * (e[k].stop - e[k].start)) / l),
      (c[g].px = { start: d, stop: d + f, width: f }),
      (d = c[g].px.stop),
      "acen" === e.stain && "p" === e.name[0] && (a.pcenIndex = g);
  a.width = d;
  a.scale = {};
  !0 === this.config.multiorganism
    ? ((a.scale.bp = 1), (a.scale.iscn = (h * l) / b.bp))
    : ((a.scale.bp = h / b.bp), (a.scale.iscn = h / b.iscn));
  a.bands = c;
  a.centromerePosition = "";
  1 == c[0].bp.stop - c[0].bp.start &&
    ((a.centromerePosition = "telocentric"), (a.bands = a.bands.slice(1)));
  return a;
};
Ideogram.prototype.drawChromosomeLabels = function (c) {
  var e = this;
  var f = e.config.chrMargin,
    d = e.config.chrWidth;
  c = e.chromosomesArray;
  var a = d / 2 + f - 8;
  "vertical" === e.config.orientation &&
    !0 === e.config.showBandLabels &&
    (a = f + 17);
  "vertical" === e.config.orientation
    ? d3
        .selectAll(".chromosome")
        .append("text")
        .data(c)
        .attr("class", "chrLabel")
        .attr("transform", "rotate(-90)")
        .attr("y", -16)
        .each(function (c, b) {
          var k = c.name.split(" ");
          var l = [];
          if (void 0 != k)
            for (
              l.push(k.slice(0, k.length - 1).join(" ")),
                l.push(k[k.length - 1]),
                e.config.showBandLabels || (b += 1),
                k = f * b,
                k = -(k + a - d - 2) + 2 * e.config.annotTracksHeight,
                b = 0;
              b < l.length;
              b++
            ) {
              var g = "";
              0 == b && e.config.fullChromosomeLabels && (g = "italic");
              d3.select(this)
                .append("tspan")
                .text(l[b])
                .attr("dy", b ? "1.2em" : 0)
                .attr("x", k)
                .attr("text-anchor", "middle")
                .attr("class", g);
            }
        })
    : d3
        .selectAll(".chromosome")
        .append("text")
        .data(c)
        .attr("class", "chrLabel")
        .attr("x", -5)
        .each(function (d, b) {
          var c = d.name.split(" ");
          var l = [];
          if (void 0 != c)
            for (
              l.push(c.slice(0, c.length - 1).join(" ")),
                l.push(c[c.length - 1]),
                c = f * b,
                c += a,
                b = 0;
              b < l.length;
              b++
            ) {
              var g = "";
              0 == b && e.config.fullChromosomeLabels && (g = "italic");
              d3.select(this)
                .append("tspan")
                .text(l[b])
                .attr("dy", b ? "1.2em" : 0)
                .attr("y", c)
                .attr("x", -8)
                .attr("text-anchor", "middle")
                .attr("class", g);
            }
        });
};
Ideogram.prototype.drawBandLabels = function (c) {
  var e = this;
  var f = [];
  for (g in c) for (a in c[g]) f.push(c[g][a]);
  var d = {};
  for (c = chrIndex = 0; c < f.length; c++) {
    chrIndex += 1;
    chrModel = f[c];
    var a = d3.select("#" + chrModel.id);
    var h = this.config.chrMargin * chrIndex;
    e = this;
    var b = h;
    1 == chrIndex &&
      "perspective" in this.config &&
      "comparative" == this.config.perspective &&
      (b += 18);
    d[chrModel.id] = [];
    a.selectAll("text")
      .data(chrModel.bands)
      .enter()
      .append("g")
      .attr("class", function (a, b) {
        return "bandLabel bsbsl-" + b;
      })
      .attr("transform", function (a) {
        a = e.round(-8 + a.px.start + a.px.width / 2);
        d[chrModel.id].push(a + 13);
        return "translate(" + a + "," + (h - 10) + ")";
      })
      .append("text")
      .text(function (a) {
        return a.name;
      });
    a.selectAll("line.bandLabelStalk")
      .data(chrModel.bands)
      .enter()
      .append("g")
      .attr("class", function (a, b) {
        return "bandLabelStalk bsbsl-" + b;
      })
      .attr("transform", function (a) {
        return (
          "translate(" + e.round(a.px.start + a.px.width / 2) + ", " + b + ")"
        );
      })
      .append("line")
      .attr("x1", 0)
      .attr("y1", 0)
      .attr("x2", 0)
      .attr("y2", -8);
  }
  for (c = 0; c < f.length; c++) {
    chrModel = f[c];
    var k = d[chrModel.id].length,
      l;
    var g = [];
    for (a = l = 0; a < k; a++) {
      var m = d[chrModel.id][a];
      if (!1 === m < l + 5) {
        g.push(a);
        if (p !== a) {
          prevTextBoxLeft = d[chrModel.id][a];
          prevTextBoxWidth = 36;
          var n = prevTextBoxLeft + prevTextBoxWidth;
        }
        if (m < n + 5) {
          var p = a;
          l = n;
        } else g.push(a);
      } else (p = a), (l = n);
    }
    k = [];
    l = g.length;
    for (m = 0; m < l; m++)
      (a = g[m]), k.push("#" + chrModel.id + " .bsbsl-" + a);
    this.bandsToShow = this.bandsToShow.concat(k);
  }
};
Ideogram.prototype.rotateChromosomeLabels = function (c, e, f, d) {
  var a = this.config.chrWidth;
  var h = this.config.chrMargin * e;
  var b = this.config.numAnnotTracks;
  var k = this;
  if (
    "undefined" === typeof d ||
    !d.hasOwnProperty("x") ||
    (1 == d.x && 1 == d.y)
  ) {
    var l = -8;
    var g = -16;
    d = { x: 1, y: 1 };
    var m = "";
  } else
    (m = "scale(" + d.x + "," + d.y + ")"),
      (l = -6),
      (g = "" === d ? -16 : -14);
  if ("vertical" == f || "" == f)
    c.selectAll("text.chrLabel")
      .attr("transform", m)
      .selectAll("tspan")
      .attr("x", l)
      .attr("y", function (d, c) {
        var g = e - 1;
        (1 < b || "" == f) && --g;
        chrMargin2 = -4;
        !0 === k.config.showBandLabels &&
          (chrMargin2 = k.config.chrMargin + a + 26);
        g *= k.config.chrMargin;
        0 == 1 < b && (g += 1);
        return g + chrMargin2;
      });
  else {
    --e;
    chrMargin2 = -a - 2;
    !0 === k.config.showBandLabels && (chrMargin2 = k.config.chrMargin + 8);
    var n = k.config.annotTracksHeight;
    "overlay" !== k.config.annotationsLayout && (n *= 2);
    c.selectAll("text.chrLabel")
      .attr("transform", "rotate(-90)" + m)
      .selectAll("tspan")
      .attr("x", function (a, b) {
        h = k.config.chrMargin * e;
        l = -(h + chrMargin2) + 3 + n;
        return (l /= d.x);
      })
      .attr("y", g);
  }
};
Ideogram.prototype.rotateBandLabels = function (c, e, f) {
  var d = this;
  var a = c.selectAll(".bandLabel");
  var h = this.config.chrMargin * e;
  var b = c.attr("data-orientation");
  if ("undefined" == typeof f) {
    f = { x: 1, y: 1 };
    var k = "";
  } else k = "scale(" + f.x + "," + f.y + ")";
  1 == e &&
  "perspective" in this.config &&
  "comparative" == this.config.perspective
    ? a
        .attr("transform", function (a) {
          var b = 8 - h - 26;
          a = d.round(2 + a.px.start + a.px.width / 2);
          return "rotate(-90)translate(" + b + "," + a + ")";
        })
        .selectAll("text")
        .attr("text-anchor", "end")
    : "vertical" == b
      ? a
          .attr("transform", function (a) {
            var b = 8 - h;
            a = d.round(2 + a.px.start + a.px.width / 2);
            return "rotate(-90)translate(" + b + "," + a + ")";
          })
          .selectAll("text")
          .attr("transform", k)
      : (a
          .attr("transform", function (a) {
            return (
              "translate(" +
              d.round(-8 * f.x + a.px.start + a.px.width / 2) +
              "," +
              (h - 10) +
              ")"
            );
          })
          .selectAll("text")
          .attr("transform", k),
        c.selectAll(".bandLabelStalk line").attr("transform", k));
};
Ideogram.prototype.round = function (c) {
  return Math.round(100 * c) / 100;
};
Ideogram.prototype.drawChromosome = function (c, e) {
  var f = this;
  var d = f.bump;
  var a = "telocentric" != c.centromerePosition ? d : Math.round(d / 4) + 3;
  var h = d3
    .select("svg")
    .append("g")
    .attr("id", c.id)
    .attr("class", "chromosome");
  var b = f.config.chrWidth;
  var k = f.config.chrMargin * e;
  h.selectAll("path")
    .data(c.bands)
    .enter()
    .append("path")
    .attr("id", function (a) {
      a = a.name.replace(".", "-");
      return c.id + "-" + a;
    })
    .attr("class", function (a) {
      var b = "band " + a.stain;
      "acen" == a.stain && (b += " " + a.name[0] + "-cen");
      return b;
    })
    .attr("d", function (e, g) {
      var h = f.round(e.px.width),
        l = f.round(e.px.start);
      var u = 0;
      if ("acen" == e.stain) {
        f.adjustedBump
          ? ((u = 0.35), (h = 0.2), (l -= 0.1), "q" === e.name[0] && (l += 1.2))
          : (h -= d / 2);
        var m = k + u;
        var n = b / 2 - 2 * u;
        u = b - 2 * u;
        "p" == e.name[0]
          ? (e =
              "M " +
              l +
              " " +
              m +
              " l " +
              h +
              " 0 q " +
              d +
              " " +
              n +
              " 0 " +
              u +
              " l -" +
              h +
              " 0 z")
          : (f.adjustedBump && (h += 0.2),
            (e =
              "M " +
              (l + h + d / 2) +
              " " +
              m +
              " l -" +
              h +
              " 0 q -" +
              (d + 0.5) +
              " " +
              n +
              " 0 " +
              u +
              " l " +
              h +
              " 0 z"));
      } else
        0 == g && ((l += a - d / 2), !0 === f.config.multiorganism && (l += a)),
          f.adjustedBump && "q" === e.name[0] && (l += 1.8),
          g == c.bands.length - 1 && (l -= a - d / 2),
          (e =
            "M " +
            l +
            " " +
            k +
            " l " +
            h +
            " 0 l 0 " +
            b +
            " l -" +
            h +
            " 0 z");
      return e;
    });
  "telocentric" != c.centromerePosition
    ? h
        .append("path")
        .attr("class", "p-ter chromosomeBorder " + c.bands[0].stain)
        .attr(
          "d",
          "M " +
            (a - d / 2 + 0.1) +
            " " +
            k +
            " q -" +
            a +
            " " +
            b / 2 +
            " 0 " +
            b,
        )
    : (h
        .append("path")
        .attr("class", "p-ter chromosomeBorder " + c.bands[0].stain)
        .attr(
          "d",
          "M " +
            (a - 3) +
            " " +
            k +
            " l -" +
            (a - 2) +
            " 0 l 0 " +
            b +
            " l " +
            (a - 2) +
            " 0 z",
        ),
      h
        .insert("path", ":first-child")
        .attr("class", "acen")
        .attr(
          "d",
          "M " +
            (a - 3) +
            " " +
            (k + 0.1 * b) +
            " l " +
            (a + d / 2 + 1) +
            " 0 l 0 " +
            0.8 * b +
            " l -" +
            (a + d / 2 + 1) +
            " 0 z",
        ));
  var l = f.adjustedBump ? 1.8 : 0;
  var g = c.pcenIndex;
  var m = c.bands[g];
  var n = c.bands[g + 1];
  0 < g
    ? ((g = m.px.start), (n = n.px.stop + l))
    : ((g = 2),
      (n = document.querySelectorAll("#" + c.id + " .band")[0].getBBox().x));
  l = n + (c.width - n + 1.3 * l) - d / 2 - 0.5;
  h.append("line")
    .attr("class", "cb-p-arm-top chromosomeBorder")
    .attr("x1", d / 2)
    .attr("y1", k)
    .attr("x2", g)
    .attr("y2", k);
  h.append("line")
    .attr("class", "cb-p-arm-bottom chromosomeBorder")
    .attr("x1", d / 2)
    .attr("y1", b + k)
    .attr("x2", g)
    .attr("y2", b + k);
  h.append("line")
    .attr("class", "cb-q-arm-top chromosomeBorder")
    .attr("x1", n)
    .attr("y1", k)
    .attr("x2", l)
    .attr("y2", k);
  h.append("line")
    .attr("class", "cb-q-arm-bottom chromosomeBorder")
    .attr("x1", n)
    .attr("y1", b + k)
    .attr("x2", l)
    .attr("y2", b + k);
  h.append("path")
    .attr(
      "class",
      "q-ter chromosomeBorder " + c.bands[c.bands.length - 1].stain,
    )
    .attr("d", "M " + l + " " + k + " q " + d + " " + b / 2 + " 0 " + b);
};
Ideogram.prototype.initTransformChromosome = function (c, e) {
  if ("vertical" == this.config.orientation) {
    var f = this.config.chrWidth;
    var d = this.config.chrMargin * e;
    this.config.showBandLabels || (e += 2);
    d += (f - 4) * (e - 1);
    c.attr("data-orientation", "vertical").attr(
      "transform",
      "rotate(90, " + (d - 30) + ", " + d + ")",
    );
    this.rotateBandLabels(c, e);
  } else c.attr("data-orientation", "horizontal");
};
Ideogram.prototype.rotateAndToggleDisplay = function (c) {
  var e = this;
  var f = d3.select("#" + c);
  var d =
    e.chromosomes[e.config.taxid][c.split("-")[0].split("chr")[1]].chrIndex;
  otherChrs = d3.selectAll("g.chromosome").filter(function (a, b) {
    return this.id !== c;
  });
  var a = e.config.orientation;
  var h = f.attr("data-orientation");
  var b = this.config.chrMargin * d;
  var k = this.config.chrWidth;
  var l = d3.select("#ideogram")[0][0].getBoundingClientRect();
  var g = l.height;
  l = l.width;
  "vertical" == a
    ? ((chrLength = f[0][0].getBoundingClientRect().height),
      (g = (l / chrLength) * 0.97),
      (scale = "scale(" + g + ", 1.5)"),
      (inverseScaleX = 2 / g),
      (inverseScaleY = 1),
      this.config.showBandLabels || (d += 2),
      (k = b + (k - 4) * (d - 1) - 30),
      (verticalTransform = "rotate(90, " + k + ", " + (k + 30) + ")"),
      (b = -1.5 * (b - this.config.annotTracksHeight)),
      this.config.showBandLabels && (b += 25),
      (horizontalTransform = "rotate(0)translate(20, " + b + ")" + scale))
    : ((chrLength = f[0][0].getBoundingClientRect().width),
      (g = (g / chrLength) * 0.97),
      (scale = "scale(" + g + ", 1.5)"),
      (inverseScaleX = 2 / g),
      (inverseScaleY = 1),
      (g = 20),
      this.config.showBandLabels || ((d += 2), (g = 15)),
      (k = b + (k - g) * (d - 2)),
      (b = k + 5),
      this.config.showBandLabels || ((k += g), (b += g)),
      (verticalTransform = "rotate(90, " + k + ", " + b + ")" + scale),
      (horizontalTransform = ""));
  inverseScale = "scale(" + inverseScaleX + "," + inverseScaleY + ")";
  "vertical" != h
    ? ("horizontal" == a && otherChrs.style("display", "none"),
      f
        .selectAll(".annot>path")
        .attr("transform", "vertical" == a ? "" : inverseScale),
      f
        .attr("data-orientation", "vertical")
        .transition()
        .attr("transform", verticalTransform)
        .each("end", function () {
          scale = "vertical" == a ? "" : { x: inverseScaleY, y: inverseScaleX };
          e.rotateBandLabels(f, d, scale);
          e.rotateChromosomeLabels(f, d, "horizontal", scale);
          "vertical" == a && otherChrs.style("display", "");
        }))
    : (f.attr("data-orientation", ""),
      "vertical" == a && otherChrs.style("display", "none"),
      f
        .selectAll(".annot>path")
        .transition()
        .attr("transform", "vertical" == a ? inverseScale : ""),
      f
        .transition()
        .attr("transform", horizontalTransform)
        .each("end", function () {
          inverseScale =
            "horizontal" == a
              ? "vertical" == h
                ? { x: 1, y: 1 }
                : ""
              : { x: inverseScaleX, y: inverseScaleY };
          e.rotateBandLabels(f, d, inverseScale);
          e.rotateChromosomeLabels(f, d, "", inverseScale);
          "horizontal" == a && otherChrs.style("display", "");
        }));
};
Ideogram.prototype.convertBpToPx = function (c, e) {
  var f;
  var d = 1;
  for (f = 0; f < c.bands.length; f++) {
    var a = c.bands[f];
    if (e >= a.bp.start && e <= a.bp.stop)
      return (
        (f = (a.iscn.stop - a.iscn.start) / (a.bp.stop - a.bp.start)),
        (d = a.iscn.start + (e - a.bp.start) * f),
        (a =
          30 +
          a.px.start +
          (a.px.width * (d - a.iscn.start)) / (a.iscn.stop - a.iscn.start))
      );
    d = a.bp.stop;
  }
  f = (a.iscn.stop - a.iscn.start) / (a.bp.stop - a.bp.start);
  d = a.iscn.start + (d - a.bp.start) * f;
  return (a =
    30 +
    a.px.start +
    (a.px.width * (d - a.iscn.start)) / (a.iscn.stop - a.iscn.start));
};
Ideogram.prototype.convertPxToBp = function (c, e) {
  var f;
  for (f = 0; f < c.bands.length; f++) {
    var d = c.bands[f];
    if (e >= d.px.start && e <= d.px.stop)
      return (
        (pxToIscnScale =
          (d.iscn.stop - d.iscn.start) / (d.px.stop - d.px.start)),
        (f = d.iscn.start + (e - d.px.start) * pxToIscnScale),
        (bp =
          d.bp.start +
          ((d.bp.stop - d.bp.start) * (f - d.iscn.start)) /
            (d.iscn.stop - d.iscn.start)),
        Math.round(bp)
      );
  }
  throw Error(
    "Pixel out of range.  px: " +
      bp +
      "; length of chr" +
      c.name +
      ": " +
      d.px.stop,
  );
};
Ideogram.prototype.drawSynteny = function (c) {
  new Date().getTime();
  var e;
  var f = d3.select("svg").append("g").attr("class", "synteny");
  for (e = 0; e < c.length; e++) {
    regions = c[e];
    var d = regions.r1;
    var a = regions.r2;
    var h = "#CFC";
    "color" in regions && (h = regions.color);
    var b = 1;
    "opacity" in regions && (b = regions.opacity);
    d.startPx = this.convertBpToPx(d.chr, d.start);
    d.stopPx = this.convertBpToPx(d.chr, d.stop);
    a.startPx = this.convertBpToPx(a.chr, a.start);
    a.stopPx = this.convertBpToPx(a.chr, a.stop);
    var k = document.querySelectorAll("#" + d.chr.id + " path")[0].getBBox();
    var l = document.querySelectorAll("#" + a.chr.id + " path")[0].getBBox();
    k = k.y - 30;
    l = l.y - 29;
    var g =
      d.chr.id +
      "_" +
      d.start +
      "_" +
      d.stop +
      "___" +
      a.chr.id +
      "_" +
      a.start +
      "_" +
      a.stop;
    syntenicRegion = f
      .append("g")
      .attr("class", "syntenicRegion")
      .attr("id", g)
      .on("click", function () {
        var a = this,
          b = d3.selectAll(".syntenicRegion").filter(function (b, d) {
            return this !== a;
          });
        b.classed("hidden", !b.classed("hidden"));
      })
      .on("mouseover", function () {
        var a = this;
        d3.selectAll(".syntenicRegion")
          .filter(function (b, d) {
            return this !== a;
          })
          .classed("ghost", !0);
      })
      .on("mouseout", function () {
        d3.selectAll(".syntenicRegion").classed("ghost", !1);
      });
    syntenicRegion
      .append("polygon")
      .attr(
        "points",
        k +
          ", " +
          d.startPx +
          " " +
          k +
          ", " +
          d.stopPx +
          " " +
          l +
          ", " +
          a.stopPx +
          " " +
          l +
          ", " +
          a.startPx,
      )
      .attr("style", "fill: " + h + "; fill-opacity: " + b);
    syntenicRegion
      .append("line")
      .attr("class", "syntenyBorder")
      .attr("x1", k)
      .attr("x2", l)
      .attr("y1", d.startPx)
      .attr("y2", a.startPx);
    syntenicRegion
      .append("line")
      .attr("class", "syntenyBorder")
      .attr("x1", k)
      .attr("x2", l)
      .attr("y1", d.stopPx)
      .attr("y2", a.stopPx);
  }
  new Date().getTime();
};
Ideogram.prototype.initAnnotSettings = function () {
  this.config.annotationsPath ||
  this.config.localAnnotationsPath ||
  this.annots ||
  this.config.annotations
    ? (this.config.annotationHeight ||
        (this.config.annotationHeight = Math.round(
          this.config.chrHeight / 100,
        )),
      (this.config.numAnnotTracks = this.config.annotationTracks
        ? this.config.annotationTracks.length
        : 1),
      (this.config.annotTracksHeight =
        this.config.annotationHeight * this.config.numAnnotTracks),
      "undefined" === typeof this.config.barWidth && (this.config.barWidth = 3))
    : (this.config.annotTracksHeight = 0);
  "undefined" === typeof this.config.annotationsColor &&
    (this.config.annotationsColor = "#000000");
};
Ideogram.prototype.drawAnnots = function (c) {
  var e,
    f = [];
  var d = this.chromosomes[this.config.taxid];
  if ("annots" in c[0]) return this.drawProcessedAnnots(c);
  for (e in d) f.push({ chr: e, annots: [] });
  for (e = 0; e < c.length; e++) {
    var a = c[e];
    for (d = 0; d < f.length; d++)
      if (a.chr === f[d].chr) {
        var h = [a.name, a.start, a.stop - a.start];
        "color" in a && h.push(a.color);
        "shape" in a && h.push(a.shape);
        f[d].annots.push(h);
        break;
      }
  }
  e = ["name", "start", "length"];
  "color" in c[0] && e.push("color");
  "shape" in c[0] && e.push("shape");
  this.rawAnnots = { keys: e, annots: f };
  this.annots = this.processAnnotData(this.rawAnnots);
  this.drawProcessedAnnots(this.annots);
};
Ideogram.prototype.processAnnotData = function (c) {
  console.log(c);
  var e = c.keys;
  c = c.annots;
  var f, d, a;
  var h = [];
  for (f = 0; 12 > f; f++) {
    var b = c[f];
    h.push({ chr: b.chr, annots: [] });
    console.log(c[f]);
    for (d = 0; d < b.annots.length; d++) {
      var k = b.chr;
      var l = b.annots[d];
      var g = {};
      for (a = 0; a < e.length; a++) g[e[a]] = l[a];
      g.stop = g.start + g.length;
      var m = this.chromosomes["4530"][k];
      a = this.convertBpToPx(m, g.start);
      m = this.convertBpToPx(m, g.stop);
      g.startPx = a;
      g.stopPx = m;
      a = Math.round((a + m) / 2) - 28;
      m = this.config.annotationsColor;
      if (this.config.annotationTracks) {
        m = this.config.allTracks;
        var n;
        for (n = 0; n < m.length; n++) {
          var p = m[n];
          p.mapping === l[3] && (g.trackIndex = p.trackIndex);
        }
        m = this.config.annotationTracks[l[3] - 1].color;
      } else g.trackIndex = -1;
      "color" in g && (m = g.color);
      g.chr = k;
      g.chrIndex = f;
      g.px = a;
      g.color = m;
      h[f].annots.push(g);
    }
  }
  return h;
};
Ideogram.prototype.getHistogramBars = function (c) {
  new Date().getTime();
  var e,
    f = !1;
  var d = [];
  var a = this.config.barWidth;
  var h = this.chromosomes[this.config.taxid];
  var b = this.config.annotationsColor;
  var k =
    "histogramScaling" in this.config
      ? this.config.histogramScaling
      : "relative";
  "undefined" === typeof this.maxAnnotsPerBar &&
    ((this.maxAnnotsPerBar = {}), (f = !0));
  for (e in h) {
    chrModel = h[e];
    var l = chrModel.chrIndex;
    lastBand = chrModel.bands[chrModel.bands.length - 1];
    var g = lastBand.px.stop;
    numBins = Math.round(g / a);
    var m = { chr: e, annots: [] };
    for (g = 0; g < numBins; g++) {
      var n = g * a - this.bump;
      bp = this.convertPxToBp(chrModel, n + this.bump);
      m.annots.push({
        bp: bp,
        px: n,
        count: 0,
        chrIndex: l,
        chrName: e,
        color: b,
      });
    }
    d.push(m);
  }
  for (e in c)
    for (
      a = c[e].annots,
        g = c[e].chr,
        chrModel = h[g],
        l = chrModel.chrIndex,
        barAnnots = d[l - 1].annots,
        g = 0;
      g < a.length;
      g++
    )
      for (n = a[g], n = n.px, b = 0; b < barAnnots.length - 1; b++) {
        m = barAnnots[b].px;
        var p = barAnnots[b + 1].px;
        if (n >= m && n < p) {
          d[l - 1].annots[b].count += 1;
          break;
        }
      }
  if (1 == f || "relative" == k) {
    for (g = h = 0; g < d.length; g++)
      for (c = d[g].annots, b = 0; b < c.length; b++)
        (barCount = c[b].count), barCount > h && (h = barCount);
    this.maxAnnotsPerBar[e] = h;
  }
  for (g = 0; g < d.length; g++)
    for (c = d[g].annots, b = 0; b < c.length; b++)
      (barCount = c[b].count),
        (height = (barCount / this.maxAnnotsPerBar[e]) * this.config.chrMargin),
        (d[g].annots[b].height = height);
  new Date().getTime();
  return d;
};
var hue = d3.scale.category20(),
  luminance = d3.scale.sqrt().domain([0, 1e6]).clamp(!0).range([90, 20]);
function fill(c) {
  return d3.lab(hue(c.name));
}
Ideogram.prototype.drawProcessedAnnots = function (c) {
  var e,
    f,
    d,
    a,
    h,
    b = this;
  var k = this.config.chrMargin;
  var l = this.config.chrWidth;
  var g = "tracks";
  this.config.annotationsLayout && (g = this.config.annotationsLayout);
  "histogram" === g && (c = b.getHistogramBars(c));
  var m = b.config.annotationHeight;
  var n = "m 0 0 l 0 " + 2 * m;
  var p = "l -" + m + " " + 2 * m + " l " + 2 * m + " 0 z";
  var r = [],
    t = !1;
  for (e = 0; e < this.numChromosomes; e++) {
    var q = ["1"];
    q.splice(0, 1);
    r.push({ chr: (e + 1).toString(), annots: q });
  }
  for (q = 0; q < c.length; q++)
    if (((e = c[q]), "asdf" === e.mapping)) {
      t = !0;
      break;
    }
  if (t) {
    for (q = 0; q < c.length; q++)
      for (e = c[q], t = 0; t < this.numChromosomes; t++)
        r[t].chr == e.chr && (r[t].annots = e.annots);
    c = d3
      .selectAll(".chromosome")
      .data(r)
      .selectAll("path.annot")
      .data(function (a) {
        return a.annots;
      })
      .enter();
  } else
    c = d3
      .selectAll(".chromosome")
      .data(c)
      .selectAll("path.annot")
      .data(function (a) {
        return a.annots;
      })
      .enter();
  "tracks" === g
    ? c
        .append("g")
        .attr("id", function (a, b) {
          return a.id;
        })
        .attr("class", function (a) {
          return 5 <= Math.ceil(a.stopPx - a.startPx) - 28
            ? "rangedannot annot"
            : "singleposition annot";
        })
        .attr("transform", function (a) {
          var b = (a.chrIndex + 1) * k + l + a.trackIndex * m * 2;
          return 5 <= Math.ceil(a.stopPx - a.startPx) - 28
            ? "translate(" + Math.ceil(a.startPx) + "," + b + ")"
            : "translate(" + a.px + "," + b + ")";
        })
        .append("path")
        .attr("d", function (a) {
          a = Math.ceil(a.stopPx - a.startPx) - 28;
          var b = Math.floor((a / 2) * Math.random()) + 4;
          return 5 <= a
            ? (n =
                "M 0 0 L 0 8 M 0 5 L " +
                a +
                " 5 M 0 4 L " +
                a +
                " 4 M " +
                a +
                " 0 L " +
                a +
                " 8M " +
                (b - 4) +
                " 4 L " +
                (b - 4) +
                " 11 L " +
                (b + 4) +
                " 11 L " +
                (b + 4) +
                " 4 Z ")
            : "m0,0" + p;
        })
        .attr("stroke-width", 1)
        .attr("stroke", function (a) {
          return 5 <= Math.ceil(a.stopPx - a.startPx) - 28 ? a.color : null;
        })
        .attr("fill", function (a) {
          return a.color;
        })
        .attr("visibility", "show")
        .attr("onclick", "showJBrowseAnnotClick()")
        .append("title")
        .html(function (a) {
          return a.name;
        })
    : "overlay" === g
      ? c
          .append("polygon")
          .attr("id", function (a, b) {
            return a.id;
          })
          .attr("class", "annot")
          .attr("points", function (b) {
            f = b.px - 0.5;
            d = b.px + 0.5;
            a = (b.chrIndex + 1) * k + l;
            h = (b.chrIndex + 1) * k;
            return (
              f +
              "," +
              a +
              " " +
              d +
              "," +
              a +
              " " +
              d +
              "," +
              h +
              " " +
              f +
              "," +
              h
            );
          })
          .attr("fill", function (a) {
            return a.color;
          })
      : "histogram" === g &&
        c
          .append("polygon")
          .attr("class", "annot")
          .attr("points", function (c) {
            f = c.px + b.bump;
            d = c.px + b.config.barWidth + b.bump;
            a = c.chrIndex * k + l;
            h = c.chrIndex * k + l + c.height;
            c = b.chromosomesArray[c.chrIndex - 1].width;
            d > c && (d = c);
            return (
              f +
              "," +
              a +
              " " +
              d +
              "," +
              a +
              " " +
              d +
              "," +
              h +
              " " +
              f +
              "," +
              h
            );
          })
          .attr("fill", function (a) {
            return a.color;
          })
          .attr("visibility", "show")
          .append("title")
          .html(function (a) {
            return a.count;
          });
  if (b.onDrawAnnotsCallback) b.onDrawAnnotsCallback();
};
Ideogram.prototype.putChromosomesInRows = function () {
  var c = this.config.rows;
  var e = Math.floor(this.numChromosomes / c);
  var f = 0;
  "g" !== d3.select("svg > *")[0][0].tagName && (f = 2);
  for (var d = 1; d < c; d++) {
    var a = e * d + 1 + f;
    var h = a + e;
    range = "nth-child(n+" + a + "):nth-child(-n+" + h + ")";
    var b = this.config.chrHeight + 20;
    a = a + 1 - f;
    h = this.config.chrWidth;
    var k = this.config.chrMargin * a;
    this.config.showBandLabels || (a += 2);
    this.config.showChromosomeLabels && (b += 12);
    rowWidth = k + (h - 4) * a + 8;
    d3.selectAll("#ideogram .chromosome:" + range).attr(
      "transform",
      function (a, c) {
        return (
          d3.select(this).attr("transform") +
          ("translate(" + b + ", " + rowWidth + ")")
        );
      },
    );
  }
};
Ideogram.prototype.onBrushMove = function () {
  call(this.onBrushMoveCallback);
};
var arrayOfBrushes = [],
  totalBrushCount = this.numChromosomes,
  chromosomeLength,
  isBrushActive = [],
  selectedRegion = [];
Ideogram.prototype.createBrush = function (c, e) {
  function f() {
    var b = arrayOfBrushes[a].extent();
    var c = Math.floor(b[0]);
    b = Math.ceil(b[1]);
    0 != c - b && (isBrushActive[a] = !0);
    selectedRegion[a] = { from: c, to: b, extent: b - c };
    if (h.onBrushMove) h.onBrushMoveCallback();
  }
  var d = 0,
    a = 0;
  for (count = 0; count < this.numChromosomes; count++) {
    e = c = "0";
    this.chromosomesArray[count].from = c;
    this.chromosomesArray[count].to = e;
    this.chromosomesArray[count].extent = null;
    var h = this;
    for (
      var b = h.config.chrWidth + 6.5,
        k = h.chromosomesArray[count],
        l = k.bands[k.bands.length - 1].bp.stop,
        g = [0],
        m = [0],
        n,
        p = 0;
      p < k.bands.length;
      p++
    )
      (n = k.bands[p]), g.push(n.bp.stop), m.push(n.px.stop);
    k = d3.scale.linear().domain(g).range(m);
    d3.select(".band")[0][0].getBBox();
    "undefined" === typeof left && (c = Math.floor(l / 10));
    "undefined" === typeof right && (e = Math.ceil(2 * c));
    selectedRegion[count] = { from: 0, to: 0, extent: 0 };
    arrayOfBrushes[count] = d3.svg.brush().y(k).extent([0, 0]).on("brush", f);
    l = d3
      .select("#ideogram")
      .append("g")
      .attr("class", "brush")
      .attr("id", "brush" + count)
      .attr("transform", "translate(" + (52.5 + d) + ", 29)")
      .call(arrayOfBrushes[count]);
    d += 76;
    l.selectAll("rect").attr("width", b);
  }
  d3.select("#ideogram")
    .append("svg:foreignObject")
    .attr("class", "dynamic-dropdown")
    .attr("id", "some-id-i-used-to-know");
  d3.select("#ideogram")
    .append("svg:foreignObject")
    .attr("class", "legend-section")
    .attr("id", "legends");
  for (p = 0; p < this.numChromosomes; p++) isBrushActive[p] = !1;
  $("#brush0").mouseenter(function () {
    a = 0;
  });
  $("#brush1").mouseenter(function () {
    a = 1;
  });
  $("#brush2").mouseenter(function () {
    a = 2;
  });
  $("#brush3").mouseenter(function () {
    a = 3;
  });
  $("#brush4").mouseenter(function () {
    a = 4;
  });
  $("#brush5").mouseenter(function () {
    a = 5;
  });
  $("#brush6").mouseenter(function () {
    a = 6;
  });
  $("#brush7").mouseenter(function () {
    a = 7;
  });
  $("#brush8").mouseenter(function () {
    a = 8;
  });
  $("#brush9").mouseenter(function () {
    a = 9;
  });
  $("#brush10").mouseenter(function () {
    a = 10;
  });
  $("#brush11").mouseenter(function () {
    a = 11;
  });
};
Ideogram.prototype.drawBrushes = function (c, e, f) {
  c = gBrushes.selectAll(".brush").data(brushes, function (c) {
    return c.id;
  });
  c.enter()
    .insert("g", ".brush")
    .attr("class", "brush")
    .attr("id", function (c) {
      return "brush-" + c.id;
    })
    .each(function (c) {
      c.brush(d3.select(this));
    });
  c.each(function (c) {
    d3.select(this)
      .attr("class", "brush")
      .selectAll(".overlay")
      .style("pointer-events", function () {
        var a = c.brush;
        return c.id === brushes.length - 1 && void 0 !== a ? "all" : "none";
      });
  });
  c.exit().remove();
};
Ideogram.prototype.onLoad = function () {
  call(this.onLoadCallback);
};
Ideogram.prototype.onDrawAnnots = function () {
  call(this.onDrawAnnotsCallback);
};
Ideogram.prototype.getBandColorGradients = function () {
  var c = "";
  var e = [
    ["gneg", "#FFF", "#FFF", "#DDD"],
    ["gpos25", "#C8C8C8", "#DDD", "#BBB"],
    ["gpos33", "#BBB", "#BBB", "#AAA"],
    ["gpos50", "#999", "#AAA", "#888"],
    ["gpos66", "#888", "#888", "#666"],
    ["gpos75", "#777", "#777", "#444"],
    ["gpos100", "#444", "#666", "#000"],
    ["acen", "#FEE", "#FEE", "#FDD"],
  ];
  for (var f = 0; f < e.length; f++) {
    var d = e[f][0];
    var a = e[f][1];
    var h = e[f][2];
    var b = e[f][3];
    c += '<linearGradient id="' + d + '" x1="0%" y1="0%" x2="0%" y2="100%">';
    c =
      "gneg" == d
        ? c +
          ('<stop offset="70%" stop-color="' +
            h +
            '" /><stop offset="95%" stop-color="' +
            b +
            '" /><stop offset="100%" stop-color="' +
            a +
            '" />')
        : c +
          ('<stop offset="5%" stop-color="' +
            a +
            '" /><stop offset="15%" stop-color="' +
            h +
            '" /><stop offset="60%" stop-color="' +
            b +
            '" />');
    c += "</linearGradient>";
  }
  return (
    '<style>.gneg {fill: url("#gneg")} .gpos25 {fill: url("#gpos25")} .gpos33 {fill: url("#gpos33")} .gpos50 {fill: url("#gpos50")} .gpos66 {fill: url("#gpos66")} .gpos75 {fill: url("#gpos75")} .gpos100 {fill: url("#gpos100")} .acen {fill: url("#acen")} .stalk {fill: url("#stalk")} .gvar {fill: url("#gvar")} </style>' +
    ("<defs>" +
      (c +
        '<pattern id="stalk" width="2" height="1" patternUnits="userSpaceOnUse" patternTransform="rotate(30 0 0)"><rect x="0" y="0" width="10" height="2" fill="#CCE" /> <line x1="0" y1="0" x2="0" y2="100%" style="stroke:#88B; stroke-width:0.7;" /></pattern><pattern id="gvar" width="2" height="1" patternUnits="userSpaceOnUse" patternTransform="rotate(-30 0 0)"><rect x="0" y="0" width="10" height="2" fill="#DDF" /> <line x1="0" y1="0" x2="0" y2="100%" style="stroke:#99C; stroke-width:0.7;" /></pattern>') +
      "</defs>")
  );
};
Ideogram.prototype.getTaxids = function () {
  var c, e;
  var f = "taxid" in this.config;
  this.config.multiorganism =
    ("organism" in this.config && this.config.organism instanceof Array) ||
    (f && this.config.taxid instanceof Array);
  var d = this.config.multiorganism;
  if ("organism" in this.config) {
    var a = d ? this.config.organism : [this.config.organism];
    var h = [];
    var b = {};
    for (e = 0; e < a.length; e++) {
      var k = a[e];
      for (c in this.organisms)
        this.organisms[c].commonName.toLowerCase() === k &&
          (h.push(c), d && (b[c] = this.config.chromosomes[k]));
    }
    this.config.taxids = h;
    d && (this.config.chromosomes = b);
  }
  d
    ? ((this.coordinateSystem = "bp"), f && (h = this.config.taxid))
    : (f && (h = [this.config.taxid]), (this.config.taxids = h));
  return h;
};
Ideogram.prototype.initDrawChromosomes = function (c) {
  var e = this.config.taxids,
    f = 0,
    d,
    a;
  for (d = 0; d < e.length; d++) {
    var h = e[d];
    var b = this.config.chromosomes[h];
    this.chromosomes[h] = {};
    for (a = 0; a < b.length; a++) {
      bands = c[f];
      f += 1;
      var k = b[a];
      var l = this.getChromosomeModel(bands, k, h, f);
      this.chromosomes[h][k] = l;
      this.chromosomesArray.push(l);
      this.drawChromosome(l, f);
    }
    !0 === this.config.showBandLabels && this.drawBandLabels(this.chromosomes);
  }
  displayLinearScale();
};
Ideogram.prototype.init = function () {
  function c() {
    b.config.annotationsPath &&
      (getTrackData(b.config.selectedTrack, b.config.annotationsPath),
      setTimeout(function () {
        b.rawAnnots = getAllTraitData();
      }, 1e3));
    b.config.rawAnnots && (b.rawAnnots = b.config.rawAnnots);
    a = "";
    b.config.showChromosomeLabels &&
      (a =
        "horizontal" == b.config.orientation
          ? a + "labeledLeft "
          : a + "labeled ");
    b.config.annotationsLayout &&
      "overlay" === b.config.annotationsLayout &&
      (a += "faint");
    if ("vertical" === b.config.orientation) {
      var c = b.config.chrHeight + 30;
      1 < b.config.rows && (c = b.config.rows * (c - 30));
    } else c = b.config.chrMargin * b.numChromosomes + 30;
    var d = b.getBandColorGradients();
    d3.select(b.config.container)
      .append("svg")
      .attr("id", "ideogram")
      .attr("class", a)
      .attr("width", "70%")
      .attr("height", c)
      .html(d);
    f();
  }
  function e() {
    var a, c;
    k = [];
    if (!0 === b.config.multiorganism) {
      b.coordinateSystem = "bp";
      var e = b.config.taxids;
      for (d = 0; d < e.length; d++);
    } else {
      "undefined" == typeof b.config.taxid &&
        (b.config.taxid = b.config.taxids[0]);
      var f = b.config.taxid;
      e = [f];
      b.config.taxids = e;
    }
    if ("chromosomes" in b.config) var g = b.config.chromosomes;
    b.config.multiorganism && (c = g);
    b.config.chromosomes = {};
    new Date().getTime();
    for (a = 0; a < e.length; a++) {
      f = e[a];
      var h = b.bandData[f];
      b.config.multiorganism && (g = c[f]);
      h = b.getBands(h, f, g);
      g = Object.keys(h);
      b.config.chromosomes[f] = g.slice();
      b.numChromosomes += b.config.chromosomes[f].length;
      for (f = 0; f < g.length; f++) {
        var l = g[f];
        var m = h[l];
        k.push(m);
        l = m[m.length - 1].iscn.stop;
        m = m[m.length - 1].bp.stop;
        l > b.maxLength.iscn && (b.maxLength.iscn = l);
        m > b.maxLength.bp && (b.maxLength.bp = m);
      }
    }
    new Date().getTime();
  }
  function f() {
    try {
      new Date().getTime();
      var a = 0,
        c,
        d,
        e;
      b.initDrawChromosomes(k);
      for (d = a = 0; d < n.length; d++)
        for (p = n[d], h = b.config.chromosomes[p], e = 0; e < h.length; e++) {
          a += 1;
          var f = h[e];
          var g = d3.select("#chr" + f + "-" + p);
          b.initTransformChromosome(g, a);
        }
      if (b.config.annotationsPath || b.rawAnnots) {
        var l = function () {
          "undefined" !== typeof timeout && window.clearTimeout(timeout);
          b.annots = b.processAnnotData(b.rawAnnots);
          b.drawProcessedAnnots(b.annots);
          b.initCrossFilter && b.initCrossFilter();
        };
        b.rawAnnots
          ? l()
          : (function v() {
              timeout = setTimeout(function () {
                b.rawAnnots ? l() : v();
              }, 50);
            })();
      }
      if (!0 === b.config.showBandLabels) {
        var m = b.bandsToShow.join(",");
        new Date().getTime();
        d3.selectAll(".bandLabel, .bandLabelStalk").style("display", "none");
        d3.selectAll(m).style("display", "");
        new Date().getTime();
        if ("vertical" === b.config.orientation)
          for (c = 0; c < b.chromosomesArray.length; c++)
            b.rotateChromosomeLabels(
              d3.select("#" + b.chromosomesArray[c].id),
              c,
            );
      }
      !0 === b.config.showChromosomeLabels &&
        b.drawChromosomeLabels(b.chromosomes);
      1 < b.config.rows && b.putChromosomesInRows();
      !0 === b.config.brush && b.createBrush();
      b.config.annotations && b.drawAnnots(b.config.annotations);
      if (b.config.armColors) {
        var r = b.config.armColors;
        b.colorArms(r[0], r[1]);
      }
      new Date().getTime();
      new Date().getTime();
      if (b.onLoadCallback) b.onLoadCallback();
      if ("rotatable" in b.config && !1 === b.config.rotatable)
        d3.selectAll(".chromosome").style("cursor", "default");
      else
        d3.selectAll(".annot").on("click", function (a, c) {
          toggleLinearScale("visible");
          var d = a.start.toString() + ".." + (a.start + a.length).toString(),
            e = "&tracks=DNA%2C" + b.config.selectedTrack + "&highlight=";
          console.log(
            "src",
            "https://snpseekv3.irri-e-extension.com/jbrowse/" + f + d + e,
          );
          var f =
            10 > a.chr
              ? "?loc=chr0" + a.chr + "%3A"
              : "?loc=chr" + a.chr + "%3A";
          $("#jbrowse").prop(
            "src",
            "https://snpseekv3.irri-e-extension.com/jbrowse/" + f + d + e,
          );
          $("#jbrowse").show();
        });
    } catch (w) {}
  }
  var d,
    a,
    h,
    b = this;
  new Date().getTime();
  var k = [],
    l = 0,
    g = this.config.resolution,
    m;
  var n = b.getTaxids();
  b.config.taxids = n;
  for (d = 0; d < n.length; d++) {
    var p = n[d];
    b.config.assembly || (b.config.assembly = "default");
    "4530" != p && (m = b.organisms[p].assemblies[b.config.assembly]);
    var r = {
      9606: "native/ideogram_9606_" + m + "_" + g + "_V1.js",
      10090: "native/ideogram_10090_" + m + "_NA_V2.js",
      7227: "ucsc/drosophila_melanogaster_dm6.tsv",
      4530: "/ideogram_4530_AP007228_850_V1.js",
    };
    "undefined" === typeof chrBands
      ? d3
          .xhr(b.config.bandDir + r[p])
          .on("beforesend", function (a) {
            a.taxid = p;
          })
          .get(function (a, d) {
            b.bandData[d.taxid] = d.response;
            l += 1;
            l == n.length && (e(), c());
          })
      : ((b.bandData[p] = chrBands), e(), c());
  }
};
