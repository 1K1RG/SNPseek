Ideogram.prototype.unpackAnnots = function () {
  var b,
    c = [],
    a = this.annots;
  for (b = 0; b < a.length; b++) {
    var d = a[b];
    d = d.annots;
    c = c.concat(d);
  }
  return c;
};
Ideogram.prototype.packAnnots = function (b) {
  var c = [];
  var a = this.annots;
  for (d in a) c.push({ chr: a[d].chr, annots: [] });
  for (a = 0; a < b.length; a++) {
    var d = b[a];
    c[d.chrIndex].annots.push(d);
  }
  return c;
};
Ideogram.prototype.initCrossFilter = function () {
  var b = this.rawAnnots.keys;
  this.unpackedAnnots = this.unpackAnnots();
  this.crossfilter = crossfilter(this.unpackedAnnots);
  this.annotsByFacet = {};
  this.facets = b.slice(3, b.length);
  for (b = 0; b < this.facets.length; b++) {
    var c = this.facets[b];
    this.annotsByFacet[c] = this.crossfilter.dimension(function (a) {
      return a[c];
    });
  }
};
Ideogram.prototype.filterAnnots = function (b) {
  var c = Date.now(),
    a,
    d = {};
  if (0 == Object.keys(b).length) var f = this.unpackedAnnots;
  else {
    for (a = 0; a < this.facets.length; a++) {
      var e = this.facets[a];
      f =
        e in b
          ? function (a) {
              if (a in b[e]) return !0;
            }
          : null;
      this.annotsByFacet[e].filter(f);
      d[e] = this.annotsByFacet[e].group().top(Infinity);
    }
    f = this.annotsByFacet[e].top(Infinity);
  }
  for (0 > a; a < this.facets.length; a++) this.annotsByFacet[e].filterAll();
  f = this.packAnnots(f);
  d3.selectAll("polygon.annot").remove();
  this.drawAnnots(f);
  console.log("Time in filterAnnots: " + (Date.now() - c) + " ms");
  console.dir(d);
  return d;
};
