--
-- PostgreSQL database dump
--

-- Dumped from database version 14.10 (Ubuntu 14.10-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 15.5 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: iric; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE iric WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF-8';


\connect iric

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


--
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;


--
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: tablefunc; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA public;


--
-- Name: EXTENSION tablefunc; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION tablefunc IS 'functions that manipulate whole tables, including crosstab';


--
-- Name: pg_schema_size(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.pg_schema_size(text) RETURNS bigint
    LANGUAGE sql
    AS $_$
SELECT SUM(pg_total_relation_size(quote_ident(schemaname) || '.' || quote_ident(tablename)))::BIGINT FROM pg_tables WHERE schemaname = $1
$_$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: analysis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.analysis (
    analysis_id integer NOT NULL,
    name character varying(255),
    description text,
    program character varying(255) NOT NULL,
    programversion character varying(255) NOT NULL,
    algorithm character varying(255),
    sourcename character varying(255),
    sourceversion character varying(255),
    sourceuri text,
    timeexecuted timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE analysis; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.analysis IS 'An analysis is a particular type of a
    computational analysis; it may be a blast of one sequence against
    another, or an all by all blast, or a different kind of analysis
    altogether. It is a single unit of computation.';


--
-- Name: COLUMN analysis.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.name IS 'A way of grouping analyses. This
    should be a handy short identifier that can help people find an
    analysis they want. For instance "tRNAscan", "cDNA", "FlyPep",
    "SwissProt", and it should not be assumed to be unique. For instance, there may be lots of separate analyses done against a cDNA database.';


--
-- Name: COLUMN analysis.program; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.program IS 'Program name, e.g. blastx, blastp, sim4, genscan.';


--
-- Name: COLUMN analysis.programversion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.programversion IS 'Version description, e.g. TBLASTX 2.0MP-WashU [09-Nov-2000].';


--
-- Name: COLUMN analysis.algorithm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.algorithm IS 'Algorithm name, e.g. blast.';


--
-- Name: COLUMN analysis.sourcename; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.sourcename IS 'Source name, e.g. cDNA, SwissProt.';


--
-- Name: COLUMN analysis.sourceuri; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysis.sourceuri IS 'This is an optional, permanent URL or URI for the source of the  analysis. The idea is that someone could recreate the analysis directly by going to this URI and fetching the source data (e.g. the blast database, or the training model).';


--
-- Name: analysis_analysis_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.analysis_analysis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: analysis_analysis_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.analysis_analysis_id_seq OWNED BY public.analysis.analysis_id;


--
-- Name: analysisfeature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.analysisfeature (
    analysisfeature_id integer NOT NULL,
    feature_id integer NOT NULL,
    analysis_id integer NOT NULL,
    rawscore double precision,
    normscore double precision,
    significance double precision,
    identity double precision
);


--
-- Name: TABLE analysisfeature; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.analysisfeature IS 'Computational analyses generate features (e.g. Genscan generates transcripts and exons; sim4 alignments generate similarity/match features). analysisfeatures are stored using the feature table from the sequence module. The analysisfeature table is used to decorate these features, with analysis specific attributes. A feature is an analysisfeature if and only if there is a corresponding entry in the analysisfeature table. analysisfeatures will have two or more featureloc entries,
 with rank indicating query/subject';


--
-- Name: COLUMN analysisfeature.rawscore; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysisfeature.rawscore IS 'This is the native score generated by the program; for example, the bitscore generated by blast, sim4 or genscan scores. One should not assume that high is necessarily better than low.';


--
-- Name: COLUMN analysisfeature.normscore; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysisfeature.normscore IS 'This is the rawscore but
    semi-normalized. Complete normalization to allow comparison of
    features generated by different programs would be nice but too
    difficult. Instead the normalization should strive to enforce the
    following semantics: * normscores are floating point numbers >= 0,
    * high normscores are better than low one. For most programs, it would be sufficient to make the normscore the same as this rawscore, providing these semantics are satisfied.';


--
-- Name: COLUMN analysisfeature.significance; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysisfeature.significance IS 'This is some kind of expectation or probability metric, representing the probability that the analysis would appear randomly given the model. As such, any program or person querying this table can assume the following semantics:
   * 0 <= significance <= n, where n is a positive number, theoretically unbounded but unlikely to be more than 10
  * low numbers are better than high numbers.';


--
-- Name: COLUMN analysisfeature.identity; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.analysisfeature.identity IS 'Percent identity between the locations compared.  Note that these 4 metrics do not cover the full range of scores possible; it would be undesirable to list every score possible, as this should be kept extensible. instead, for non-standard scores, use the analysisprop table.';


--
-- Name: analysisfeature_analysisfeature_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.analysisfeature_analysisfeature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: analysisfeature_analysisfeature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.analysisfeature_analysisfeature_id_seq OWNED BY public.analysisfeature.analysisfeature_id;


--
-- Name: chadoprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chadoprop (
    chadoprop_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE chadoprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.chadoprop IS 'This table is different from other prop tables in the database, as it is for storing information about the database itself, like schema version';


--
-- Name: COLUMN chadoprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chadoprop.type_id IS 'The name of the property or slot is a cvterm. The meaning of the property is defined in that cvterm.';


--
-- Name: COLUMN chadoprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chadoprop.value IS 'The value of the property, represented as text. Numeric values are converted to their text representation.';


--
-- Name: COLUMN chadoprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chadoprop.rank IS 'Property-Value ordering. Any
cv can have multiple values for any particular property type -
these are ordered in a list using rank, counting from zero. For
properties that are single-valued rather than multi-valued, the
default 0 value should be used.';


--
-- Name: chadoprop_chadoprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.chadoprop_chadoprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: chadoprop_chadoprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.chadoprop_chadoprop_id_seq OWNED BY public.chadoprop.chadoprop_id;


--
-- Name: cv; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cv (
    cv_id integer NOT NULL,
    name character varying(255) NOT NULL,
    definition text
);


--
-- Name: TABLE cv; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cv IS 'A controlled vocabulary or ontology. A cv is
composed of cvterms (AKA terms, classes, types, universals - relations
and properties are also stored in cvterm) and the relationships
between them.';


--
-- Name: COLUMN cv.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cv.name IS 'The name of the ontology. This
corresponds to the obo-format -namespace-. cv names uniquely identify
the cv. In OBO file format, the cv.name is known as the namespace.';


--
-- Name: COLUMN cv.definition; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cv.definition IS 'A text description of the criteria for
membership of this ontology.';


--
-- Name: cv_cv_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cv_cv_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cv_cv_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cv_cv_id_seq OWNED BY public.cv.cv_id;


--
-- Name: cvprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvprop (
    cvprop_id integer NOT NULL,
    cv_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE cvprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvprop IS 'Additional extensible properties can be attached to a cv using this table.  A notable example would be the cv version';


--
-- Name: COLUMN cvprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvprop.type_id IS 'The name of the property or slot is a cvterm. The meaning of the property is defined in that cvterm.';


--
-- Name: COLUMN cvprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvprop.value IS 'The value of the property, represented as text. Numeric values are converted to their text representation.';


--
-- Name: COLUMN cvprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvprop.rank IS 'Property-Value ordering. Any
cv can have multiple values for any particular property type -
these are ordered in a list using rank, counting from zero. For
properties that are single-valued rather than multi-valued, the
default 0 value should be used.';


--
-- Name: cvprop_cvprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvprop_cvprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvprop_cvprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvprop_cvprop_id_seq OWNED BY public.cvprop.cvprop_id;


--
-- Name: cvterm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvterm (
    cvterm_id integer NOT NULL,
    cv_id integer NOT NULL,
    name character varying(1024) NOT NULL,
    definition text,
    dbxref_id integer NOT NULL,
    is_obsolete integer DEFAULT 0 NOT NULL,
    is_relationshiptype integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE cvterm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvterm IS 'A term, class, universal or type within an
ontology or controlled vocabulary.  This table is also used for
relations and properties. cvterms constitute nodes in the graph
defined by the collection of cvterms and cvterm_relationships.';


--
-- Name: COLUMN cvterm.cv_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.cv_id IS 'The cv or ontology or namespace to which
this cvterm belongs.';


--
-- Name: COLUMN cvterm.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.name IS 'A concise human-readable name or
label for the cvterm. Uniquely identifies a cvterm within a cv.';


--
-- Name: COLUMN cvterm.definition; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.definition IS 'A human-readable text
definition.';


--
-- Name: COLUMN cvterm.dbxref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.dbxref_id IS 'Primary identifier dbxref - The
unique global OBO identifier for this cvterm.  Note that a cvterm may
have multiple secondary dbxrefs - see also table: cvterm_dbxref.';


--
-- Name: COLUMN cvterm.is_obsolete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.is_obsolete IS 'Boolean 0=false,1=true; see
GO documentation for details of obsoletion. Note that two terms with
different primary dbxrefs may exist if one is obsolete.';


--
-- Name: COLUMN cvterm.is_relationshiptype; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm.is_relationshiptype IS 'Boolean
0=false,1=true relations or relationship types (also known as Typedefs
in OBO format, or as properties or slots) form a cv/ontology in
themselves. We use this flag to indicate whether this cvterm is an
actual term/class/universal or a relation. Relations may be drawn from
the OBO Relations ontology, but are not exclusively drawn from there.';


--
-- Name: cvterm_cvterm_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvterm_cvterm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvterm_cvterm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvterm_cvterm_id_seq OWNED BY public.cvterm.cvterm_id;


--
-- Name: cvterm_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvterm_dbxref (
    cvterm_dbxref_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    dbxref_id integer NOT NULL,
    is_for_definition integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE cvterm_dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvterm_dbxref IS 'In addition to the primary
identifier (cvterm.dbxref_id) a cvterm can have zero or more secondary
identifiers/dbxrefs, which may refer to records in external
databases. The exact semantics of cvterm_dbxref are not fixed. For
example: the dbxref could be a pubmed ID that is pertinent to the
cvterm, or it could be an equivalent or similar term in another
ontology. For example, GO cvterms are typically linked to InterPro
IDs, even though the nature of the relationship between them is
largely one of statistical association. The dbxref may be have data
records attached in the same database instance, or it could be a
"hanging" dbxref pointing to some external database. NOTE: If the
desired objective is to link two cvterms together, and the nature of
the relation is known and holds for all instances of the subject
cvterm then consider instead using cvterm_relationship together with a
well-defined relation.';


--
-- Name: COLUMN cvterm_dbxref.is_for_definition; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm_dbxref.is_for_definition IS 'A
cvterm.definition should be supported by one or more references. If
this column is true, the dbxref is not for a term in an external database -
it is a dbxref for provenance information for the definition.';


--
-- Name: cvterm_dbxref_cvterm_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvterm_dbxref_cvterm_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvterm_dbxref_cvterm_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvterm_dbxref_cvterm_dbxref_id_seq OWNED BY public.cvterm_dbxref.cvterm_dbxref_id;


--
-- Name: cvterm_relationship; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvterm_relationship (
    cvterm_relationship_id integer NOT NULL,
    type_id integer NOT NULL,
    subject_id integer NOT NULL,
    object_id integer NOT NULL
);


--
-- Name: TABLE cvterm_relationship; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvterm_relationship IS 'A relationship linking two
cvterms. Each cvterm_relationship constitutes an edge in the graph
defined by the collection of cvterms and cvterm_relationships. The
meaning of the cvterm_relationship depends on the definition of the
cvterm R refered to by type_id. However, in general the definitions
are such that the statement "all SUBJs REL some OBJ" is true. The
cvterm_relationship statement is about the subject, not the
object. For example "insect wing part_of thorax".';


--
-- Name: COLUMN cvterm_relationship.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm_relationship.type_id IS 'The nature of the
relationship between subject and object. Note that relations are also
housed in the cvterm table, typically from the OBO relationship
ontology, although other relationship types are allowed.';


--
-- Name: COLUMN cvterm_relationship.subject_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm_relationship.subject_id IS 'The subject of
the subj-predicate-obj sentence. The cvterm_relationship is about the
subject. In a graph, this typically corresponds to the child node.';


--
-- Name: COLUMN cvterm_relationship.object_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvterm_relationship.object_id IS 'The object of the
subj-predicate-obj sentence. The cvterm_relationship refers to the
object. In a graph, this typically corresponds to the parent node.';


--
-- Name: cvterm_relationship_cvterm_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvterm_relationship_cvterm_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvterm_relationship_cvterm_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvterm_relationship_cvterm_relationship_id_seq OWNED BY public.cvterm_relationship.cvterm_relationship_id;


--
-- Name: cvtermpath; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvtermpath (
    cvtermpath_id integer NOT NULL,
    type_id integer,
    subject_id integer NOT NULL,
    object_id integer NOT NULL,
    cv_id integer NOT NULL,
    pathdistance integer
);


--
-- Name: TABLE cvtermpath; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvtermpath IS 'The reflexive transitive closure of
the cvterm_relationship relation.';


--
-- Name: COLUMN cvtermpath.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermpath.type_id IS 'The relationship type that
this is a closure over. If null, then this is a closure over ALL
relationship types. If non-null, then this references a relationship
cvterm - note that the closure will apply to both this relationship
AND the OBO_REL:is_a (subclass) relationship.';


--
-- Name: COLUMN cvtermpath.cv_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermpath.cv_id IS 'Closures will mostly be within
one cv. If the closure of a relationship traverses a cv, then this
refers to the cv of the object_id cvterm.';


--
-- Name: COLUMN cvtermpath.pathdistance; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermpath.pathdistance IS 'The number of steps
required to get from the subject cvterm to the object cvterm, counting
from zero (reflexive relationship).';


--
-- Name: cvtermpath_cvtermpath_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvtermpath_cvtermpath_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvtermpath_cvtermpath_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvtermpath_cvtermpath_id_seq OWNED BY public.cvtermpath.cvtermpath_id;


--
-- Name: cvtermprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvtermprop (
    cvtermprop_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    type_id integer NOT NULL,
    value text DEFAULT ''::text NOT NULL,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE cvtermprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvtermprop IS 'Additional extensible properties can be attached to a cvterm using this table. Corresponds to -AnnotationProperty- in W3C OWL format.';


--
-- Name: COLUMN cvtermprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermprop.type_id IS 'The name of the property or slot is a cvterm. The meaning of the property is defined in that cvterm.';


--
-- Name: COLUMN cvtermprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermprop.value IS 'The value of the property, represented as text. Numeric values are converted to their text representation.';


--
-- Name: COLUMN cvtermprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermprop.rank IS 'Property-Value ordering. Any
cvterm can have multiple values for any particular property type -
these are ordered in a list using rank, counting from zero. For
properties that are single-valued rather than multi-valued, the
default 0 value should be used.';


--
-- Name: cvtermprop_cvtermprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvtermprop_cvtermprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvtermprop_cvtermprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvtermprop_cvtermprop_id_seq OWNED BY public.cvtermprop.cvtermprop_id;


--
-- Name: cvtermsynonym; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cvtermsynonym (
    cvtermsynonym_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    synonym character varying(1024) NOT NULL,
    type_id integer
);


--
-- Name: TABLE cvtermsynonym; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cvtermsynonym IS 'A cvterm actually represents a
distinct class or concept. A concept can be refered to by different
phrases or names. In addition to the primary name (cvterm.name) there
can be a number of alternative aliases or synonyms. For example, "T
cell" as a synonym for "T lymphocyte".';


--
-- Name: COLUMN cvtermsynonym.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.cvtermsynonym.type_id IS 'A synonym can be exact,
narrower, or broader than.';


--
-- Name: cvtermsynonym_cvtermsynonym_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cvtermsynonym_cvtermsynonym_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cvtermsynonym_cvtermsynonym_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cvtermsynonym_cvtermsynonym_id_seq OWNED BY public.cvtermsynonym.cvtermsynonym_id;


--
-- Name: db; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.db (
    db_id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    urlprefix character varying(255),
    url character varying(255)
);


--
-- Name: TABLE db; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.db IS 'A database authority. Typical databases in
bioinformatics are FlyBase, GO, UniProt, NCBI, MGI, etc. The authority
is generally known by this shortened form, which is unique within the
bioinformatics and biomedical realm.  To Do - add support for URIs,
URNs (e.g. LSIDs). We can do this by treating the URL as a URI -
however, some applications may expect this to be resolvable - to be
decided.';


--
-- Name: db_db_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.db_db_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: db_db_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.db_db_id_seq OWNED BY public.db.db_id;


--
-- Name: dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dbxref (
    dbxref_id integer NOT NULL,
    db_id integer NOT NULL,
    accession character varying(255) NOT NULL,
    version character varying(255) DEFAULT ''::character varying NOT NULL,
    description text
);


--
-- Name: TABLE dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.dbxref IS 'A unique, global, public, stable identifier. Not necessarily an external reference - can reference data items inside the particular chado instance being used. Typically a row in a table can be uniquely identified with a primary identifier (called dbxref_id); a table may also have secondary identifiers (in a linking table <T>_dbxref). A dbxref is generally written as <DB>:<ACCESSION> or as <DB>:<ACCESSION>:<VERSION>.';


--
-- Name: COLUMN dbxref.accession; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.dbxref.accession IS 'The local part of the identifier. Guaranteed by the db authority to be unique for that db.';


--
-- Name: dbxref_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dbxref_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dbxref_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.dbxref_dbxref_id_seq OWNED BY public.dbxref.dbxref_id;


--
-- Name: dbxrefprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dbxrefprop (
    dbxrefprop_id integer NOT NULL,
    dbxref_id integer NOT NULL,
    type_id integer NOT NULL,
    value text DEFAULT ''::text NOT NULL,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE dbxrefprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.dbxrefprop IS 'Metadata about a dbxref. Note that this is not defined in the dbxref module, as it depends on the cvterm table. This table has a structure analagous to cvtermprop.';


--
-- Name: dbxrefprop_dbxrefprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dbxrefprop_dbxrefprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dbxrefprop_dbxrefprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.dbxrefprop_dbxrefprop_id_seq OWNED BY public.dbxrefprop.dbxrefprop_id;


--
-- Name: feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature (
    feature_id integer NOT NULL,
    dbxref_id integer,
    organism_id integer NOT NULL,
    name character varying(255),
    uniquename text NOT NULL,
    residues text,
    seqlen integer,
    md5checksum character(32),
    type_id integer NOT NULL,
    is_analysis boolean DEFAULT false NOT NULL,
    is_obsolete boolean DEFAULT false NOT NULL,
    timeaccessioned timestamp without time zone DEFAULT now() NOT NULL,
    timelastmodified timestamp without time zone DEFAULT now() NOT NULL
);
ALTER TABLE ONLY public.feature ALTER COLUMN residues SET STORAGE EXTERNAL;


--
-- Name: TABLE feature; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature IS 'A feature is a biological sequence or a
section of a biological sequence, or a collection of such
sections. Examples include genes, exons, transcripts, regulatory
regions, polypeptides, protein domains, chromosome sequences, sequence
variations, cross-genome match regions such as hits and HSPs and so
on; see the Sequence Ontology for more. The combination of
organism_id, uniquename and type_id should be unique.';


--
-- Name: COLUMN feature.dbxref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.dbxref_id IS 'An optional primary public stable
identifier for this feature. Secondary identifiers and external
dbxrefs go in the table feature_dbxref.';


--
-- Name: COLUMN feature.organism_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.organism_id IS 'The organism to which this feature
belongs. This column is mandatory.';


--
-- Name: COLUMN feature.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.name IS 'The optional human-readable common name for
a feature, for display purposes.';


--
-- Name: COLUMN feature.uniquename; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.uniquename IS 'The unique name for a feature; may
not be necessarily be particularly human-readable, although this is
preferred. This name must be unique for this type of feature within
this organism.';


--
-- Name: COLUMN feature.residues; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.residues IS 'A sequence of alphabetic characters
representing biological residues (nucleic acids, amino acids). This
column does not need to be manifested for all features; it is optional
for features such as exons where the residues can be derived from the
featureloc. It is recommended that the value for this column be
manifested for features which may may non-contiguous sublocations (e.g.
transcripts), since derivation at query time is non-trivial. For
expressed sequence, the DNA sequence should be used rather than the
RNA sequence. The default storage method for the residues column is
EXTERNAL, which will store it uncompressed to make substring operations
faster.';


--
-- Name: COLUMN feature.seqlen; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.seqlen IS 'The length of the residue feature. See
column:residues. This column is partially redundant with the residues
column, and also with featureloc. This column is required because the
location may be unknown and the residue sequence may not be
manifested, yet it may be desirable to store and query the length of
the feature. The seqlen should always be manifested where the length
of the sequence is known.';


--
-- Name: COLUMN feature.md5checksum; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.md5checksum IS 'The 32-character checksum of the sequence,
calculated using the MD5 algorithm. This is practically guaranteed to
be unique for any feature. This column thus acts as a unique
identifier on the mathematical sequence.';


--
-- Name: COLUMN feature.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.type_id IS 'A required reference to a table:cvterm
giving the feature type. This will typically be a Sequence Ontology
identifier. This column is thus used to subclass the feature table.';


--
-- Name: COLUMN feature.is_analysis; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.is_analysis IS 'Boolean indicating whether this
feature is annotated or the result of an automated analysis. Analysis
results also use the companalysis module. Note that the dividing line
between analysis and annotation may be fuzzy, this should be determined on
a per-project basis in a consistent manner. One requirement is that
there should only be one non-analysis version of each wild-type gene
feature in a genome, whereas the same gene feature can be predicted
multiple times in different analyses.';


--
-- Name: COLUMN feature.is_obsolete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.is_obsolete IS 'Boolean indicating whether this
feature has been obsoleted. Some chado instances may choose to simply
remove the feature altogether, others may choose to keep an obsolete
row in the table.';


--
-- Name: COLUMN feature.timeaccessioned; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.timeaccessioned IS 'For handling object
accession or modification timestamps (as opposed to database auditing data,
handled elsewhere). The expectation is that these fields would be
available to software interacting with chado.';


--
-- Name: COLUMN feature.timelastmodified; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature.timelastmodified IS 'For handling object
accession or modification timestamps (as opposed to database auditing data,
handled elsewhere). The expectation is that these fields would be
available to software interacting with chado.';


--
-- Name: feature_cvterm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_cvterm (
    feature_cvterm_id integer NOT NULL,
    feature_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    pub_id integer NOT NULL,
    is_not boolean DEFAULT false NOT NULL,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_cvterm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_cvterm IS 'Associate a term from a cv with a feature, for example, GO annotation.';


--
-- Name: COLUMN feature_cvterm.pub_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_cvterm.pub_id IS 'Provenance for the annotation. Each annotation should have a single primary publication (which may be of the appropriate type for computational analyses) where more details can be found. Additional provenance dbxrefs can be attached using feature_cvterm_dbxref.';


--
-- Name: COLUMN feature_cvterm.is_not; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_cvterm.is_not IS 'If this is set to true, then this annotation is interpreted as a NEGATIVE annotation - i.e. the feature does NOT have the specified function, process, component, part, etc. See GO docs for more details.';


--
-- Name: feature_cvterm_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_cvterm_dbxref (
    feature_cvterm_dbxref_id integer NOT NULL,
    feature_cvterm_id integer NOT NULL,
    dbxref_id integer NOT NULL
);


--
-- Name: TABLE feature_cvterm_dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_cvterm_dbxref IS 'Additional dbxrefs for an association. Rows in the feature_cvterm table may be backed up by dbxrefs. For example, a feature_cvterm association that was inferred via a protein-protein interaction may be backed by by refering to the dbxref for the alternate protein. Corresponds to the WITH column in a GO gene association file (but can also be used for other analagous associations). See http://www.geneontology.org/doc/GO.annotation.shtml#file for more details.';


--
-- Name: feature_cvterm_dbxref_feature_cvterm_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_cvterm_dbxref_feature_cvterm_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_cvterm_dbxref_feature_cvterm_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_cvterm_dbxref_feature_cvterm_dbxref_id_seq OWNED BY public.feature_cvterm_dbxref.feature_cvterm_dbxref_id;


--
-- Name: feature_cvterm_feature_cvterm_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_cvterm_feature_cvterm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_cvterm_feature_cvterm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_cvterm_feature_cvterm_id_seq OWNED BY public.feature_cvterm.feature_cvterm_id;


--
-- Name: feature_cvterm_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_cvterm_pub (
    feature_cvterm_pub_id integer NOT NULL,
    feature_cvterm_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE feature_cvterm_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_cvterm_pub IS 'Secondary pubs for an
association. Each feature_cvterm association is supported by a single
primary publication. Additional secondary pubs can be added using this
linking table (in a GO gene association file, these corresponding to
any IDs after the pipe symbol in the publications column.';


--
-- Name: feature_cvterm_pub_feature_cvterm_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_cvterm_pub_feature_cvterm_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_cvterm_pub_feature_cvterm_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_cvterm_pub_feature_cvterm_pub_id_seq OWNED BY public.feature_cvterm_pub.feature_cvterm_pub_id;


--
-- Name: feature_cvtermprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_cvtermprop (
    feature_cvtermprop_id integer NOT NULL,
    feature_cvterm_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_cvtermprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_cvtermprop IS 'Extensible properties for
feature to cvterm associations. Examples: GO evidence codes;
qualifiers; metadata such as the date on which the entry was curated
and the source of the association. See the featureprop table for
meanings of type_id, value and rank.';


--
-- Name: COLUMN feature_cvtermprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_cvtermprop.type_id IS 'The name of the
property/slot is a cvterm. The meaning of the property is defined in
that cvterm. cvterms may come from the OBO evidence code cv.';


--
-- Name: COLUMN feature_cvtermprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_cvtermprop.value IS 'The value of the
property, represented as text. Numeric values are converted to their
text representation. This is less efficient than using native database
types, but is easier to query.';


--
-- Name: COLUMN feature_cvtermprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_cvtermprop.rank IS 'Property-Value
ordering. Any feature_cvterm can have multiple values for any particular
property type - these are ordered in a list using rank, counting from
zero. For properties that are single-valued rather than multi-valued,
the default 0 value should be used.';


--
-- Name: feature_cvtermprop_feature_cvtermprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_cvtermprop_feature_cvtermprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_cvtermprop_feature_cvtermprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_cvtermprop_feature_cvtermprop_id_seq OWNED BY public.feature_cvtermprop.feature_cvtermprop_id;


--
-- Name: feature_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_dbxref (
    feature_dbxref_id integer NOT NULL,
    feature_id integer NOT NULL,
    dbxref_id integer NOT NULL,
    is_current boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE feature_dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_dbxref IS 'Links a feature to dbxrefs. This is for secondary identifiers; primary identifiers should use feature.dbxref_id.';


--
-- Name: COLUMN feature_dbxref.is_current; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_dbxref.is_current IS 'True if this secondary dbxref is the most up to date accession in the corresponding db. Retired accessions should set this field to false';


--
-- Name: feature_dbxref_feature_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_dbxref_feature_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_dbxref_feature_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_dbxref_feature_dbxref_id_seq OWNED BY public.feature_dbxref.feature_dbxref_id;


--
-- Name: feature_expression; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_expression (
    feature_expression_id integer NOT NULL,
    expression_id integer NOT NULL,
    feature_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: feature_expression_feature_expression_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_expression_feature_expression_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_expression_feature_expression_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_expression_feature_expression_id_seq OWNED BY public.feature_expression.feature_expression_id;


--
-- Name: feature_expressionprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_expressionprop (
    feature_expressionprop_id integer NOT NULL,
    feature_expression_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_expressionprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_expressionprop IS 'Extensible properties for
feature_expression (comments, for example). Modeled on feature_cvtermprop.';


--
-- Name: feature_expressionprop_feature_expressionprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_expressionprop_feature_expressionprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_expressionprop_feature_expressionprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_expressionprop_feature_expressionprop_id_seq OWNED BY public.feature_expressionprop.feature_expressionprop_id;


--
-- Name: feature_feature_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_feature_id_seq
    START WITH 7651244
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_feature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_feature_id_seq OWNED BY public.feature.feature_id;


--
-- Name: feature_genotype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_genotype (
    feature_genotype_id integer NOT NULL,
    feature_id integer NOT NULL,
    genotype_id integer NOT NULL,
    chromosome_id integer,
    rank integer NOT NULL,
    cgroup integer NOT NULL,
    cvterm_id integer NOT NULL
);


--
-- Name: COLUMN feature_genotype.chromosome_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_genotype.chromosome_id IS 'A feature of SO type "chromosome".';


--
-- Name: COLUMN feature_genotype.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_genotype.rank IS 'rank can be used for
n-ploid organisms or to preserve order.';


--
-- Name: COLUMN feature_genotype.cgroup; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_genotype.cgroup IS 'Spatially distinguishable
group. group can be used for distinguishing the chromosomal groups,
for example (RNAi products and so on can be treated as different
groups, as they do not fall on a particular chromosome).';


--
-- Name: feature_genotype_feature_genotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_genotype_feature_genotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_genotype_feature_genotype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_genotype_feature_genotype_id_seq OWNED BY public.feature_genotype.feature_genotype_id;


--
-- Name: feature_phenotype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_phenotype (
    feature_phenotype_id integer NOT NULL,
    feature_id integer NOT NULL,
    phenotype_id integer NOT NULL
);


--
-- Name: feature_phenotype_feature_phenotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_phenotype_feature_phenotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_phenotype_feature_phenotype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_phenotype_feature_phenotype_id_seq OWNED BY public.feature_phenotype.feature_phenotype_id;


--
-- Name: feature_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_pub (
    feature_pub_id integer NOT NULL,
    feature_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE feature_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_pub IS 'Provenance. Linking table between features and publications that mention them.';


--
-- Name: feature_pub_feature_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_pub_feature_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_pub_feature_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_pub_feature_pub_id_seq OWNED BY public.feature_pub.feature_pub_id;


--
-- Name: feature_pubprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_pubprop (
    feature_pubprop_id integer NOT NULL,
    feature_pub_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_pubprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_pubprop IS 'Property or attribute of a feature_pub link.';


--
-- Name: feature_pubprop_feature_pubprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_pubprop_feature_pubprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_pubprop_feature_pubprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_pubprop_feature_pubprop_id_seq OWNED BY public.feature_pubprop.feature_pubprop_id;


--
-- Name: feature_relationship; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_relationship (
    feature_relationship_id integer NOT NULL,
    subject_id integer NOT NULL,
    object_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_relationship; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_relationship IS 'Features can be arranged in
graphs, e.g. "exon part_of transcript part_of gene"; If type is
thought of as a verb, the each arc or edge makes a statement
[Subject Verb Object]. The object can also be thought of as parent
(containing feature), and subject as child (contained feature or
subfeature). We include the relationship rank/order, because even
though most of the time we can order things implicitly by sequence
coordinates, we can not always do this - e.g. transpliced genes. It is also
useful for quickly getting implicit introns.';


--
-- Name: COLUMN feature_relationship.subject_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationship.subject_id IS 'The subject of the subj-predicate-obj sentence. This is typically the subfeature.';


--
-- Name: COLUMN feature_relationship.object_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationship.object_id IS 'The object of the subj-predicate-obj sentence. This is typically the container feature.';


--
-- Name: COLUMN feature_relationship.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationship.type_id IS 'Relationship type between subject and object. This is a cvterm, typically from the OBO relationship ontology, although other relationship types are allowed. The most common relationship type is OBO_REL:part_of. Valid relationship types are constrained by the Sequence Ontology.';


--
-- Name: COLUMN feature_relationship.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationship.value IS 'Additional notes or comments.';


--
-- Name: COLUMN feature_relationship.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationship.rank IS 'The ordering of subject features with respect to the object feature may be important (for example, exon ordering on a transcript - not always derivable if you take trans spliced genes into consideration). Rank is used to order these; starts from zero.';


--
-- Name: feature_relationship_feature_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_relationship_feature_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_relationship_feature_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_relationship_feature_relationship_id_seq OWNED BY public.feature_relationship.feature_relationship_id;


--
-- Name: feature_relationship_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_relationship_pub (
    feature_relationship_pub_id integer NOT NULL,
    feature_relationship_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE feature_relationship_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_relationship_pub IS 'Provenance. Attach optional evidence to a feature_relationship in the form of a publication.';


--
-- Name: feature_relationship_pub_feature_relationship_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_relationship_pub_feature_relationship_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_relationship_pub_feature_relationship_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_relationship_pub_feature_relationship_pub_id_seq OWNED BY public.feature_relationship_pub.feature_relationship_pub_id;


--
-- Name: feature_relationshipprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_relationshipprop (
    feature_relationshipprop_id integer NOT NULL,
    feature_relationship_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE feature_relationshipprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_relationshipprop IS 'Extensible properties
for feature_relationships. Analagous structure to featureprop. This
table is largely optional and not used with a high frequency. Typical
scenarios may be if one wishes to attach additional data to a
feature_relationship - for example to say that the
feature_relationship is only true in certain contexts.';


--
-- Name: COLUMN feature_relationshipprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationshipprop.type_id IS 'The name of the
property/slot is a cvterm. The meaning of the property is defined in
that cvterm. Currently there is no standard ontology for
feature_relationship property types.';


--
-- Name: COLUMN feature_relationshipprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationshipprop.value IS 'The value of the
property, represented as text. Numeric values are converted to their
text representation. This is less efficient than using native database
types, but is easier to query.';


--
-- Name: COLUMN feature_relationshipprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_relationshipprop.rank IS 'Property-Value
ordering. Any feature_relationship can have multiple values for any particular
property type - these are ordered in a list using rank, counting from
zero. For properties that are single-valued rather than multi-valued,
the default 0 value should be used.';


--
-- Name: feature_relationshipprop_feature_relationshipprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_relationshipprop_feature_relationshipprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_relationshipprop_feature_relationshipprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_relationshipprop_feature_relationshipprop_id_seq OWNED BY public.feature_relationshipprop.feature_relationshipprop_id;


--
-- Name: feature_relationshipprop_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_relationshipprop_pub (
    feature_relationshipprop_pub_id integer NOT NULL,
    feature_relationshipprop_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE feature_relationshipprop_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_relationshipprop_pub IS 'Provenance for feature_relationshipprop.';


--
-- Name: feature_relationshipprop_pub_feature_relationshipprop_pub_i_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_relationshipprop_pub_feature_relationshipprop_pub_i_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_relationshipprop_pub_feature_relationshipprop_pub_i_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.feature_relationshipprop_pub_feature_relationshipprop_pub_i_seq OWNED BY public.feature_relationshipprop_pub.feature_relationshipprop_pub_id;


--
-- Name: feature_synonym_feature_synonym_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.feature_synonym_feature_synonym_id_seq
    START WITH 456980
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: feature_synonym; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feature_synonym (
    feature_synonym_id integer DEFAULT nextval('public.feature_synonym_feature_synonym_id_seq'::regclass) NOT NULL,
    synonym_id integer NOT NULL,
    feature_id integer NOT NULL,
    pub_id integer NOT NULL,
    is_current boolean DEFAULT false NOT NULL,
    is_internal boolean DEFAULT false NOT NULL
);


--
-- Name: TABLE feature_synonym; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.feature_synonym IS 'Linking table between feature and synonym.';


--
-- Name: COLUMN feature_synonym.pub_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_synonym.pub_id IS 'The pub_id link is for relating the usage of a given synonym to the publication in which it was used.';


--
-- Name: COLUMN feature_synonym.is_current; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_synonym.is_current IS 'The is_current boolean indicates whether the linked synonym is the  current -official- symbol for the linked feature.';


--
-- Name: COLUMN feature_synonym.is_internal; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.feature_synonym.is_internal IS 'Typically a synonym exists so that somebody querying the db with an obsolete name can find the object theyre looking for (under its current name.  If the synonym has been used publicly and deliberately (e.g. in a paper), it may also be listed in reports as a synonym. If the synonym was not used deliberately (e.g. there was a typo which went public), then the is_internal boolean may be set to -true- so that it is known that the synonym is -internal- and should be queryable but should not be listed in reports as a valid synonym.';


--
-- Name: featureloc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.featureloc (
    featureloc_id integer NOT NULL,
    feature_id integer NOT NULL,
    srcfeature_id integer,
    fmin integer,
    is_fmin_partial boolean DEFAULT false NOT NULL,
    fmax integer,
    is_fmax_partial boolean DEFAULT false NOT NULL,
    strand smallint,
    phase integer,
    residue_info text,
    locgroup integer DEFAULT 0 NOT NULL,
    rank integer DEFAULT 0 NOT NULL,
    CONSTRAINT featureloc_c2 CHECK ((fmin <= fmax))
);


--
-- Name: TABLE featureloc; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.featureloc IS 'The location of a feature relative to
another feature. Important: interbase coordinates are used. This is
vital as it allows us to represent zero-length features e.g. splice
sites, insertion points without an awkward fuzzy system. Features
typically have exactly ONE location, but this need not be the
case. Some features may not be localized (e.g. a gene that has been
characterized genetically but no sequence or molecular information is
available). Note on multiple locations: Each feature can have 0 or
more locations. Multiple locations do NOT indicate non-contiguous
locations (if a feature such as a transcript has a non-contiguous
location, then the subfeatures such as exons should always be
manifested). Instead, multiple featurelocs for a feature designate
alternate locations or grouped locations; for instance, a feature
designating a blast hit or hsp will have two locations, one on the
query feature, one on the subject feature. Features representing
sequence variation could have alternate locations instantiated on a
feature on the mutant strain. The column:rank is used to
differentiate these different locations. Reflexive locations should
never be stored - this is for -proper- (i.e. non-self) locations only; nothing should be located relative to itself.';


--
-- Name: COLUMN featureloc.feature_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.feature_id IS 'The feature that is being located. Any feature can have zero or more featurelocs.';


--
-- Name: COLUMN featureloc.srcfeature_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.srcfeature_id IS 'The source feature which this location is relative to. Every location is relative to another feature (however, this column is nullable, because the srcfeature may not be known). All locations are -proper- that is, nothing should be located relative to itself. No cycles are allowed in the featureloc graph.';


--
-- Name: COLUMN featureloc.fmin; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.fmin IS 'The leftmost/minimal boundary in the linear range represented by the featureloc. Sometimes (e.g. in Bioperl) this is called -start- although this is confusing because it does not necessarily represent the 5-prime coordinate. Important: This is space-based (interbase) coordinates, counting from zero. To convert this to the leftmost position in a base-oriented system (eg GFF, Bioperl), add 1 to fmin.';


--
-- Name: COLUMN featureloc.is_fmin_partial; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.is_fmin_partial IS 'This is typically
false, but may be true if the value for column:fmin is inaccurate or
the leftmost part of the range is unknown/unbounded.';


--
-- Name: COLUMN featureloc.fmax; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.fmax IS 'The rightmost/maximal boundary in the linear range represented by the featureloc. Sometimes (e.g. in bioperl) this is called -end- although this is confusing because it does not necessarily represent the 3-prime coordinate. Important: This is space-based (interbase) coordinates, counting from zero. No conversion is required to go from fmax to the rightmost coordinate in a base-oriented system that counts from 1 (e.g. GFF, Bioperl).';


--
-- Name: COLUMN featureloc.is_fmax_partial; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.is_fmax_partial IS 'This is typically
false, but may be true if the value for column:fmax is inaccurate or
the rightmost part of the range is unknown/unbounded.';


--
-- Name: COLUMN featureloc.strand; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.strand IS 'The orientation/directionality of the
location. Should be 0, -1 or +1.';


--
-- Name: COLUMN featureloc.phase; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.phase IS 'Phase of translation with
respect to srcfeature_id.
Values are 0, 1, 2. It may not be possible to manifest this column for
some features such as exons, because the phase is dependant on the
spliceform (the same exon can appear in multiple spliceforms). This column is mostly useful for predicted exons and CDSs.';


--
-- Name: COLUMN featureloc.residue_info; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.residue_info IS 'Alternative residues,
when these differ from feature.residues. For instance, a SNP feature
located on a wild and mutant protein would have different alternative residues.
for alignment/similarity features, the alternative residues is used to
represent the alignment string (CIGAR format). Note on variation
features; even if we do not want to instantiate a mutant
chromosome/contig feature, we can still represent a SNP etc with 2
locations, one (rank 0) on the genome, the other (rank 1) would have
most fields null, except for alternative residues.';


--
-- Name: COLUMN featureloc.locgroup; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.locgroup IS 'This is used to manifest redundant,
derivable extra locations for a feature. The default locgroup=0 is
used for the DIRECT location of a feature. Important: most Chado users may
never use featurelocs WITH logroup > 0. Transitively derived locations
are indicated with locgroup > 0. For example, the position of an exon on
a BAC and in global chromosome coordinates. This column is used to
differentiate these groupings of locations. The default locgroup 0
is used for the main or primary location, from which the others can be
derived via coordinate transformations. Another example of redundant
locations is storing ORF coordinates relative to both transcript and
genome. Redundant locations open the possibility of the database
getting into inconsistent states; this schema gives us the flexibility
of both warehouse instantiations with redundant locations (easier for
querying) and management instantiations with no redundant
locations. An example of using both locgroup and rank: imagine a
feature indicating a conserved region between the chromosomes of two
different species. We may want to keep redundant locations on both
contigs and chromosomes. We would thus have 4 locations for the single
conserved region feature - two distinct locgroups (contig level and
chromosome level) and two distinct ranks (for the two species).';


--
-- Name: COLUMN featureloc.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureloc.rank IS 'Used when a feature has >1
location, otherwise the default rank 0 is used. Some features (e.g.
blast hits and HSPs) have two locations - one on the query and one on
the subject. Rank is used to differentiate these. Rank=0 is always
used for the query, Rank=1 for the subject. For multiple alignments,
assignment of rank is arbitrary. Rank is also used for
sequence_variant features, such as SNPs. Rank=0 indicates the wildtype
(or baseline) feature, Rank=1 indicates the mutant (or compared) feature.';


--
-- Name: featureloc_featureloc_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.featureloc_featureloc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: featureloc_featureloc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.featureloc_featureloc_id_seq OWNED BY public.featureloc.featureloc_id;


--
-- Name: featureloc_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.featureloc_pub (
    featureloc_pub_id integer NOT NULL,
    featureloc_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE featureloc_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.featureloc_pub IS 'Provenance of featureloc. Linking table between featurelocs and publications that mention them.';


--
-- Name: featureloc_pub_featureloc_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.featureloc_pub_featureloc_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: featureloc_pub_featureloc_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.featureloc_pub_featureloc_pub_id_seq OWNED BY public.featureloc_pub.featureloc_pub_id;


--
-- Name: featureprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.featureprop (
    featureprop_id integer NOT NULL,
    feature_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE featureprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.featureprop IS 'A feature can have any number of slot-value property tags attached to it. This is an alternative to hardcoding a list of columns in the relational schema, and is completely extensible.';


--
-- Name: COLUMN featureprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureprop.type_id IS 'The name of the
property/slot is a cvterm. The meaning of the property is defined in
that cvterm. Certain property types will only apply to certain feature
types (e.g. the anticodon property will only apply to tRNA features) ;
the types here come from the sequence feature property ontology.';


--
-- Name: COLUMN featureprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureprop.value IS 'The value of the property, represented as text. Numeric values are converted to their text representation. This is less efficient than using native database types, but is easier to query.';


--
-- Name: COLUMN featureprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.featureprop.rank IS 'Property-Value ordering. Any
feature can have multiple values for any particular property type -
these are ordered in a list using rank, counting from zero. For
properties that are single-valued rather than multi-valued, the
default 0 value should be used';


--
-- Name: featureprop_featureprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.featureprop_featureprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: featureprop_featureprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.featureprop_featureprop_id_seq OWNED BY public.featureprop.featureprop_id;


--
-- Name: featureprop_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.featureprop_pub (
    featureprop_pub_id integer NOT NULL,
    featureprop_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE featureprop_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.featureprop_pub IS 'Provenance. Any featureprop assignment can optionally be supported by a publication.';


--
-- Name: featureprop_pub_featureprop_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.featureprop_pub_featureprop_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: featureprop_pub_featureprop_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.featureprop_pub_featureprop_pub_id_seq OWNED BY public.featureprop_pub.featureprop_pub_id;


--
-- Name: gene; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.gene AS
 SELECT feature.feature_id AS gene_id,
    feature.feature_id,
    feature.dbxref_id,
    feature.organism_id,
    feature.name,
    feature.residues,
    feature.seqlen,
    feature.md5checksum,
    feature.type_id,
    feature.is_analysis,
    feature.is_obsolete,
    feature.timeaccessioned,
    feature.timelastmodified,
    feature.uniquename
   FROM (public.feature
     JOIN public.cvterm ON ((feature.type_id = cvterm.cvterm_id)))
  WHERE ((cvterm.name)::text = 'gene'::text);


--
-- Name: genotype_run; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.genotype_run (
    genotype_run_id integer NOT NULL,
    platform_id integer,
    date_performed date,
    data_location character varying(255),
    visible boolean DEFAULT true
);


--
-- Name: genotype_run_genotype_run_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.genotype_run_genotype_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: genotype_run_genotype_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.genotype_run_genotype_run_id_seq OWNED BY public.genotype_run.genotype_run_id;


--
-- Name: genotyping_run_genotyping_run_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.genotyping_run_genotyping_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: gff_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gff_meta (
    name character varying(100),
    hostname character varying(100),
    starttime timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: gff_sort_tmp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gff_sort_tmp (
    refseq character varying(4000),
    id character varying(4000),
    parent character varying(4000),
    gffline character varying(8000),
    row_id integer NOT NULL
);


--
-- Name: gff_sort_tmp_row_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.gff_sort_tmp_row_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: gff_sort_tmp_row_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.gff_sort_tmp_row_id_seq OWNED BY public.gff_sort_tmp.row_id;


--
-- Name: gwas_run; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gwas_run (
    gwas_run_id integer NOT NULL,
    trait_id bigint,
    subpopulation_id bigint
);


--
-- Name: gwas_subpopulation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gwas_subpopulation (
    gwas_subpopulation_id integer NOT NULL,
    name character varying(255)
);


--
-- Name: gwas_trait; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gwas_trait (
    gwas_trait_id bigint NOT NULL,
    name character varying(1024),
    definition character varying(4000),
    phenotype_id integer
);


--
-- Name: indel_feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.indel_feature (
    indel_feature_id integer NOT NULL
);


--
-- Name: indel_feature_indel_feature_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.indel_feature_indel_feature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: indel_feature_indel_feature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.indel_feature_indel_feature_id_seq OWNED BY public.indel_feature.indel_feature_id;


--
-- Name: indel_featureloc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.indel_featureloc (
    indel_featureloc_id integer NOT NULL,
    indel_feature_id integer,
    organism_id integer,
    srcfeature_id integer,
    "position" integer,
    refcall character varying,
    altcall character varying,
    max_insert_len integer,
    max_delete_len integer
);


--
-- Name: indel_featureloc_indel_featureloc_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.indel_featureloc_indel_featureloc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: indel_featureloc_indel_featureloc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.indel_featureloc_indel_featureloc_id_seq OWNED BY public.indel_featureloc.indel_featureloc_id;


--
-- Name: locus_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.locus_mapping (
    locus_mapping_id bigint,
    name character varying(255),
    msu7 character varying(255),
    rap_representative character varying(255),
    rap_predicted character varying(255),
    fgenesh character varying(255)
);


--
-- Name: locus_mapping_feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.locus_mapping_feature (
    locus_mapping_feature_id bigint,
    locus_mapping_id bigint,
    feature_id bigint,
    db_id bigint
);


--
-- Name: materialized_view; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.materialized_view (
    materialized_view_id integer NOT NULL,
    last_update timestamp without time zone,
    refresh_time integer,
    name character varying(64),
    mv_schema character varying(64),
    mv_table character varying(128),
    mv_specs text,
    indexed text,
    query text,
    special_index text
);


--
-- Name: materialized_view_materialized_view_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.materialized_view_materialized_view_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: materialized_view_materialized_view_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.materialized_view_materialized_view_id_seq OWNED BY public.materialized_view.materialized_view_id;


--
-- Name: mv_convertpos_nb2allrefs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mv_convertpos_nb2allrefs (
    mv_convertpos_nb2allrefs_id integer NOT NULL,
    snp_feature_id integer NOT NULL,
    from_organism_id integer NOT NULL,
    from_contig_id integer NOT NULL,
    from_position integer,
    from_refcall character varying(1),
    nb_contig_id integer,
    nb_position integer,
    nb_refcall character varying(1),
    nb_align_count integer,
    ir64_contig_id integer,
    ir64_position integer,
    ir64_refcall character varying(1),
    ir64_align_count integer,
    rice9311_contig_id integer,
    rice9311_position integer,
    rice9311_refcall character varying(1),
    rice9311_align_count integer,
    dj123_contig_id integer,
    dj123_position integer,
    dj123_refcall character varying(1),
    dj123_align_count integer,
    kasalath_contig_id integer,
    kasalath_position integer,
    kasalath_refcall character varying(1),
    kasalath_align_count integer
);


--
-- Name: mv_convertpos_nb2allrefs_mv_convertpos_nb2allrefs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.mv_convertpos_nb2allrefs_mv_convertpos_nb2allrefs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: mv_convertpos_nb2allrefs_mv_convertpos_nb2allrefs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.mv_convertpos_nb2allrefs_mv_convertpos_nb2allrefs_id_seq OWNED BY public.mv_convertpos_nb2allrefs.mv_convertpos_nb2allrefs_id;


--
-- Name: snp_featureloc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.snp_featureloc (
    snp_featureloc_id integer NOT NULL,
    snp_feature_id integer,
    organism_id integer,
    srcfeature_id integer,
    "position" integer,
    refcall character(1)
);


--
-- Name: COLUMN snp_featureloc."position"; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.snp_featureloc."position" IS '0-base (interbase) bp position';


--
-- Name: variant_variantset; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.variant_variantset (
    variant_variantset_id integer NOT NULL,
    variant_feature_id integer NOT NULL,
    variant_type_id integer,
    variantset_id integer NOT NULL,
    hdf5_index integer
);


--
-- Name: variantset_variantset_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.variantset_variantset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: variantset; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.variantset (
    variantset_id integer DEFAULT nextval('public.variantset_variantset_id_seq'::regclass) NOT NULL,
    name character varying(255),
    description text,
    variant_type_id integer NOT NULL,
    organism_id integer DEFAULT 9
);


--
-- Name: mv_snp_refposindex; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.mv_snp_refposindex AS
 SELECT sfl.snp_feature_id,
    (srcf.feature_id - 2) AS chromosome,
    sfl."position",
    sfl.refcall,
    ''::character varying(1) AS altcall,
    vvs.hdf5_index AS allele_index,
    v.variantset_id AS type_id,
    v.name AS variantset
   FROM public.snp_featureloc sfl,
    public.feature srcf,
    public.variant_variantset vvs,
    public.variantset v
  WHERE ((sfl.snp_feature_id = vvs.variant_feature_id) AND (sfl.srcfeature_id = srcf.feature_id) AND (vvs.variantset_id = v.variantset_id));


--
-- Name: organism; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organism (
    organism_id integer NOT NULL,
    abbreviation character varying(255),
    genus character varying(255) NOT NULL,
    species character varying(255) NOT NULL,
    common_name character varying(255),
    comment text
);


--
-- Name: TABLE organism; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.organism IS 'The organismal taxonomic
classification. Note that phylogenies are represented using the
phylogeny module, and taxonomies can be represented using the cvterm
module or the phylogeny module.';


--
-- Name: COLUMN organism.species; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.organism.species IS 'A type of organism is always
uniquely identified by genus and species. When mapping from the NCBI
taxonomy names.dmp file, this column must be used where it
is present, as the common_name column is not always unique (e.g. environmental
samples). If a particular strain or subspecies is to be represented,
this is appended onto the species name. Follows standard NCBI taxonomy
pattern.';


--
-- Name: organism_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organism_dbxref (
    organism_dbxref_id integer NOT NULL,
    organism_id integer NOT NULL,
    dbxref_id integer NOT NULL
);


--
-- Name: organism_dbxref_organism_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.organism_dbxref_organism_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: organism_dbxref_organism_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.organism_dbxref_organism_dbxref_id_seq OWNED BY public.organism_dbxref.organism_dbxref_id;


--
-- Name: organism_organism_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.organism_organism_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: organism_organism_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.organism_organism_id_seq OWNED BY public.organism.organism_id;


--
-- Name: organismprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organismprop (
    organismprop_id integer NOT NULL,
    organism_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE organismprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.organismprop IS 'Tag-value properties - follows standard chado model.';


--
-- Name: organismprop_organismprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.organismprop_organismprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: organismprop_organismprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.organismprop_organismprop_id_seq OWNED BY public.organismprop.organismprop_id;


--
-- Name: platform; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.platform (
    platform_id integer NOT NULL,
    variantset_id integer,
    db_id integer,
    genotyping_method_id integer
);


--
-- Name: platform_platform_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.platform_platform_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: platform_platform_id_seq1; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.platform_platform_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: platform_platform_id_seq1; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.platform_platform_id_seq1 OWNED BY public.platform.platform_id;


--
-- Name: pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pub (
    pub_id integer NOT NULL,
    title text,
    volumetitle text,
    volume character varying(255),
    series_name character varying(255),
    issue character varying(255),
    pyear character varying(255),
    pages character varying(255),
    miniref character varying(255),
    uniquename text NOT NULL,
    type_id integer NOT NULL,
    is_obsolete boolean DEFAULT false,
    publisher character varying(255),
    pubplace character varying(255)
);


--
-- Name: TABLE pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.pub IS 'A documented provenance artefact - publications,
documents, personal communication.';


--
-- Name: COLUMN pub.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pub.title IS 'Descriptive general heading.';


--
-- Name: COLUMN pub.volumetitle; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pub.volumetitle IS 'Title of part if one of a series.';


--
-- Name: COLUMN pub.series_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pub.series_name IS 'Full name of (journal) series.';


--
-- Name: COLUMN pub.pages; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pub.pages IS 'Page number range[s], e.g. 457--459, viii + 664pp, lv--lvii.';


--
-- Name: COLUMN pub.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pub.type_id IS 'The type of the publication (book, journal, poem, graffiti, etc). Uses pub cv.';


--
-- Name: pub_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pub_dbxref (
    pub_dbxref_id integer NOT NULL,
    pub_id integer NOT NULL,
    dbxref_id integer NOT NULL,
    is_current boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE pub_dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.pub_dbxref IS 'Handle links to repositories,
e.g. Pubmed, Biosis, zoorec, OCLC, Medline, ISSN, coden...';


--
-- Name: pub_dbxref_pub_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pub_dbxref_pub_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pub_dbxref_pub_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pub_dbxref_pub_dbxref_id_seq OWNED BY public.pub_dbxref.pub_dbxref_id;


--
-- Name: pub_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pub_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pub_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pub_pub_id_seq OWNED BY public.pub.pub_id;


--
-- Name: pub_relationship; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pub_relationship (
    pub_relationship_id integer NOT NULL,
    subject_id integer NOT NULL,
    object_id integer NOT NULL,
    type_id integer NOT NULL
);


--
-- Name: TABLE pub_relationship; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.pub_relationship IS 'Handle relationships between
publications, e.g. when one publication makes others obsolete, when one
publication contains errata with respect to other publication(s), or
when one publication also appears in another pub.';


--
-- Name: pub_relationship_pub_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pub_relationship_pub_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pub_relationship_pub_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pub_relationship_pub_relationship_id_seq OWNED BY public.pub_relationship.pub_relationship_id;


--
-- Name: pubauthor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pubauthor (
    pubauthor_id integer NOT NULL,
    pub_id integer NOT NULL,
    rank integer NOT NULL,
    editor boolean DEFAULT false,
    surname character varying(100) NOT NULL,
    givennames character varying(100),
    suffix character varying(100)
);


--
-- Name: TABLE pubauthor; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.pubauthor IS 'An author for a publication. Note the denormalisation (hence lack of _ in table name) - this is deliberate as it is in general too hard to assign IDs to authors.';


--
-- Name: COLUMN pubauthor.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pubauthor.rank IS 'Order of author in author list for this pub - order is important.';


--
-- Name: COLUMN pubauthor.editor; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pubauthor.editor IS 'Indicates whether the author is an editor for linked publication. Note: this is a boolean field but does not follow the normal chado convention for naming booleans.';


--
-- Name: COLUMN pubauthor.givennames; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pubauthor.givennames IS 'First name, initials';


--
-- Name: COLUMN pubauthor.suffix; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.pubauthor.suffix IS 'Jr., Sr., etc';


--
-- Name: pubauthor_pubauthor_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pubauthor_pubauthor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pubauthor_pubauthor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pubauthor_pubauthor_id_seq OWNED BY public.pubauthor.pubauthor_id;


--
-- Name: pubprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pubprop (
    pubprop_id integer NOT NULL,
    pub_id integer NOT NULL,
    type_id integer NOT NULL,
    value text NOT NULL,
    rank integer
);


--
-- Name: TABLE pubprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.pubprop IS 'Property-value pairs for a pub. Follows standard chado pattern.';


--
-- Name: pubprop_pubprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pubprop_pubprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pubprop_pubprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pubprop_pubprop_id_seq OWNED BY public.pubprop.pubprop_id;


--
-- Name: sample_run_sample_run_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sample_run_sample_run_id_seq
    START WITH 10853
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sample_varietyset; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sample_varietyset (
    sample_varietyset_id integer DEFAULT nextval('public.sample_run_sample_run_id_seq'::regclass) NOT NULL,
    stock_sample_id integer,
    db_id integer,
    hdf5_index integer
);


--
-- Name: seed_order; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.seed_order (
    seed_order_id integer NOT NULL,
    requestor_name character varying(255),
    institution character varying(255),
    address1 character varying(1024),
    address2 character varying(1024),
    country character varying(255),
    requestor_email character varying(255),
    requestor_phone character varying(255),
    user_category character varying(16),
    country_category character varying(16),
    smta_acceptance character varying(16),
    authorized_name character varying(255),
    authorized_position character varying(255),
    authorized_email character varying(255),
    authorized_phone character varying(255),
    postal_code character varying(255),
    date_ordered date,
    status character varying(16),
    total_price numeric,
    verify_code character varying(16),
    order_code character varying(16)
);


--
-- Name: COLUMN seed_order.user_category; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.seed_order.user_category IS 'irri, private, public';


--
-- Name: COLUMN seed_order.country_category; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.seed_order.country_category IS 'low, lowmid, upmid, high';


--
-- Name: COLUMN seed_order.smta_acceptance; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.seed_order.smta_acceptance IS 'shrink-wrap, signed';


--
-- Name: COLUMN seed_order.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.seed_order.status IS 'submitted, verified, approved, delivered';


--
-- Name: seed_order_seed_order_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.seed_order_seed_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seed_order_seed_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.seed_order_seed_order_id_seq OWNED BY public.seed_order.seed_order_id;


--
-- Name: snp_feature; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.snp_feature (
    snp_feature_id integer NOT NULL,
    variantset_id integer
);


--
-- Name: snp_feature_snp_feature_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.snp_feature_snp_feature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: snp_feature_snp_feature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.snp_feature_snp_feature_id_seq OWNED BY public.snp_feature.snp_feature_id;


--
-- Name: snp_featureloc_snp_featureloc_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.snp_featureloc_snp_featureloc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: snp_featureloc_snp_featureloc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.snp_featureloc_snp_featureloc_id_seq OWNED BY public.snp_featureloc.snp_featureloc_id;


--
-- Name: snp_featureprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.snp_featureprop (
    snp_featureprop_id integer NOT NULL,
    snp_feature_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: snp_featureprop_snp_featureprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.snp_featureprop_snp_featureprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: snp_featureprop_snp_featureprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.snp_featureprop_snp_featureprop_id_seq OWNED BY public.snp_featureprop.snp_featureprop_id;


--
-- Name: snp_genotype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.snp_genotype (
    snp_feature_id integer,
    stock_sample_id integer,
    genotype_run_id integer,
    allele1 bpchar,
    allele2 bpchar
);


--
-- Name: snp_genotype_snp_genotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.snp_genotype_snp_genotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: snp_genotype_wis9100Mhdra; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."snp_genotype_wis9100Mhdra" (
    snp_feature_id integer,
    stock_sample_id integer,
    genotype_run_id integer,
    allele1 bpchar,
    allele2 bpchar
);


--
-- Name: stock; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock (
    stock_id integer NOT NULL,
    dbxref_id integer,
    organism_id integer,
    name character varying(255),
    uniquename text NOT NULL,
    description text,
    type_id integer NOT NULL,
    is_obsolete boolean DEFAULT false NOT NULL,
    stock_geolocation_id integer,
    tmp_oldstock_id integer
);


--
-- Name: TABLE stock; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock IS 'Any stock can be globally identified by the
combination of organism, uniquename and stock type. A stock is the physical entities, either living or preserved, held by collections. Stocks belong to a collection; they have IDs, type, organism, description and may have a genotype.';


--
-- Name: COLUMN stock.dbxref_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock.dbxref_id IS 'The dbxref_id is an optional primary stable identifier for this stock. Secondary indentifiers and external dbxrefs go in table: stock_dbxref.';


--
-- Name: COLUMN stock.organism_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock.organism_id IS 'The organism_id is the organism to which the stock belongs. This column should only be left blank if the organism cannot be determined.';


--
-- Name: COLUMN stock.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock.name IS 'The name is a human-readable local name for a stock.';


--
-- Name: COLUMN stock.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock.description IS 'The description is the genetic description provided in the stock list.';


--
-- Name: COLUMN stock.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock.type_id IS 'The type_id foreign key links to a controlled vocabulary of stock types. The would include living stock, genomic DNA, preserved specimen. Secondary cvterms for stocks would go in stock_cvterm.';


--
-- Name: stock_cvterm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_cvterm (
    stock_cvterm_id integer NOT NULL,
    stock_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    pub_id integer NOT NULL,
    is_not boolean DEFAULT false NOT NULL,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE stock_cvterm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_cvterm IS 'stock_cvterm links a stock to cvterms. This is for secondary cvterms; primary cvterms should use stock.type_id.';


--
-- Name: stock_cvterm_stock_cvterm_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_cvterm_stock_cvterm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_cvterm_stock_cvterm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_cvterm_stock_cvterm_id_seq OWNED BY public.stock_cvterm.stock_cvterm_id;


--
-- Name: stock_cvtermprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_cvtermprop (
    stock_cvtermprop_id integer NOT NULL,
    stock_cvterm_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE stock_cvtermprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_cvtermprop IS 'Extensible properties for
stock to cvterm associations. Examples: GO evidence codes;
qualifiers; metadata such as the date on which the entry was curated
and the source of the association. See the stockprop table for
meanings of type_id, value and rank.';


--
-- Name: COLUMN stock_cvtermprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_cvtermprop.type_id IS 'The name of the
property/slot is a cvterm. The meaning of the property is defined in
that cvterm. cvterms may come from the OBO evidence code cv.';


--
-- Name: COLUMN stock_cvtermprop.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_cvtermprop.value IS 'The value of the
property, represented as text. Numeric values are converted to their
text representation. This is less efficient than using native database
types, but is easier to query.';


--
-- Name: COLUMN stock_cvtermprop.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_cvtermprop.rank IS 'Property-Value
ordering. Any stock_cvterm can have multiple values for any particular
property type - these are ordered in a list using rank, counting from
zero. For properties that are single-valued rather than multi-valued,
the default 0 value should be used.';


--
-- Name: stock_cvtermprop_stock_cvtermprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_cvtermprop_stock_cvtermprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_cvtermprop_stock_cvtermprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_cvtermprop_stock_cvtermprop_id_seq OWNED BY public.stock_cvtermprop.stock_cvtermprop_id;


--
-- Name: stock_dbxref; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_dbxref (
    stock_dbxref_id integer NOT NULL,
    stock_id integer NOT NULL,
    dbxref_id integer NOT NULL,
    is_current boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE stock_dbxref; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_dbxref IS 'stock_dbxref links a stock to dbxrefs. This is for secondary identifiers; primary identifiers should use stock.dbxref_id.';


--
-- Name: COLUMN stock_dbxref.is_current; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_dbxref.is_current IS 'The is_current boolean indicates whether the linked dbxref is the current -official- dbxref for the linked stock.';


--
-- Name: stock_dbxref_stock_dbxref_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_dbxref_stock_dbxref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_dbxref_stock_dbxref_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_dbxref_stock_dbxref_id_seq OWNED BY public.stock_dbxref.stock_dbxref_id;


--
-- Name: stock_dbxrefprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_dbxrefprop (
    stock_dbxrefprop_id integer NOT NULL,
    stock_dbxref_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE stock_dbxrefprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_dbxrefprop IS 'A stock_dbxref can have any number of
slot-value property tags attached to it. This is useful for storing properties related to dbxref annotations of stocks, such as evidence codes, and references, and metadata, such as create/modify dates. This is an alternative to
hardcoding a list of columns in the relational schema, and is
completely extensible. There is a unique constraint, stock_dbxrefprop_c1, for
the combination of stock_dbxref_id, rank, and type_id. Multivalued property-value pairs must be differentiated by rank.';


--
-- Name: stock_dbxrefprop_stock_dbxrefprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_dbxrefprop_stock_dbxrefprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_dbxrefprop_stock_dbxrefprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_dbxrefprop_stock_dbxrefprop_id_seq OWNED BY public.stock_dbxrefprop.stock_dbxrefprop_id;


--
-- Name: stock_genotype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_genotype (
    stock_genotype_id integer NOT NULL,
    stock_id integer NOT NULL,
    genotype_id integer NOT NULL
);


--
-- Name: TABLE stock_genotype; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_genotype IS 'Simple table linking a stock to
a genotype. Features with genotypes can be linked to stocks thru feature_genotype -> genotype -> stock_genotype -> stock.';


--
-- Name: stock_genotype_stock_genotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_genotype_stock_genotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_genotype_stock_genotype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_genotype_stock_genotype_id_seq OWNED BY public.stock_genotype.stock_genotype_id;


--
-- Name: stock_phenotype; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_phenotype (
    stock_phenotype_id integer NOT NULL,
    stock_id integer,
    dbxref_id integer,
    type_id integer,
    quan_value numeric,
    qual_value character varying(255),
    stock_type_id integer
);


--
-- Name: stock_phenotype2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_phenotype2 (
    stock_phenotype2_id bigint NOT NULL,
    stock_id integer,
    type_id integer,
    quan_value numeric,
    qual_value character varying(255)
);


--
-- Name: stock_phenotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_phenotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_phenotype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_phenotype_id_seq OWNED BY public.stock_phenotype.stock_phenotype_id;


--
-- Name: stock_phenotype_stock_phenotype_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_phenotype_stock_phenotype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_phenotype_stock_phenotype_id_seq1; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_phenotype_stock_phenotype_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_phenotype_stock_phenotype_id_seq1; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_phenotype_stock_phenotype_id_seq1 OWNED BY public.stock_phenotype.stock_phenotype_id;


--
-- Name: stock_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_pub (
    stock_pub_id integer NOT NULL,
    stock_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE stock_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_pub IS 'Provenance. Linking table between stocks and, for example, a stocklist computer file.';


--
-- Name: stock_pub_stock_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_pub_stock_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_pub_stock_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_pub_stock_pub_id_seq OWNED BY public.stock_pub.stock_pub_id;


--
-- Name: stock_relationship; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_relationship (
    stock_relationship_id integer NOT NULL,
    subject_id integer NOT NULL,
    object_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: COLUMN stock_relationship.subject_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_relationship.subject_id IS 'stock_relationship.subject_id is the subject of the subj-predicate-obj sentence. This is typically the substock.';


--
-- Name: COLUMN stock_relationship.object_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_relationship.object_id IS 'stock_relationship.object_id is the object of the subj-predicate-obj sentence. This is typically the container stock.';


--
-- Name: COLUMN stock_relationship.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_relationship.type_id IS 'stock_relationship.type_id is relationship type between subject and object. This is a cvterm, typically from the OBO relationship ontology, although other relationship types are allowed.';


--
-- Name: COLUMN stock_relationship.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_relationship.value IS 'stock_relationship.value is for additional notes or comments.';


--
-- Name: COLUMN stock_relationship.rank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stock_relationship.rank IS 'stock_relationship.rank is the ordering of subject stocks with respect to the object stock may be important where rank is used to order these; starts from zero.';


--
-- Name: stock_relationship_cvterm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_relationship_cvterm (
    stock_relationship_cvterm_id integer NOT NULL,
    stock_relationship_id integer NOT NULL,
    cvterm_id integer NOT NULL,
    pub_id integer
);


--
-- Name: TABLE stock_relationship_cvterm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_relationship_cvterm IS 'For germplasm maintenance and pedigree data, stock_relationship. type_id will record cvterms such as "is a female parent of", "a parent for mutation", "is a group_id of", "is a source_id of", etc The cvterms for higher categories such as "generative", "derivative" or "maintenance" can be stored in table stock_relationship_cvterm';


--
-- Name: stock_relationship_cvterm_stock_relationship_cvterm_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_relationship_cvterm_stock_relationship_cvterm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_relationship_cvterm_stock_relationship_cvterm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_relationship_cvterm_stock_relationship_cvterm_id_seq OWNED BY public.stock_relationship_cvterm.stock_relationship_cvterm_id;


--
-- Name: stock_relationship_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_relationship_pub (
    stock_relationship_pub_id integer NOT NULL,
    stock_relationship_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE stock_relationship_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stock_relationship_pub IS 'Provenance. Attach optional evidence to a stock_relationship in the form of a publication.';


--
-- Name: stock_relationship_pub_stock_relationship_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_relationship_pub_stock_relationship_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_relationship_pub_stock_relationship_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_relationship_pub_stock_relationship_pub_id_seq OWNED BY public.stock_relationship_pub.stock_relationship_pub_id;


--
-- Name: stock_relationship_stock_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_relationship_stock_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_relationship_stock_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_relationship_stock_relationship_id_seq OWNED BY public.stock_relationship.stock_relationship_id;


--
-- Name: stock_sample; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stock_sample (
    stock_sample_id integer NOT NULL,
    stock_id integer,
    dbxref_id integer,
    hdf5_index integer,
    tmp_oldstock_id integer
);


--
-- Name: stock_sample_stock_sample_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_sample_stock_sample_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_sample_stock_sample_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_sample_stock_sample_id_seq OWNED BY public.stock_sample.stock_sample_id;


--
-- Name: stock_stock_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stock_stock_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stock_stock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stock_stock_id_seq OWNED BY public.stock.stock_id;


--
-- Name: stockcollection; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stockcollection (
    stockcollection_id integer NOT NULL,
    type_id integer NOT NULL,
    contact_id integer,
    name character varying(255),
    uniquename text NOT NULL
);


--
-- Name: TABLE stockcollection; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stockcollection IS 'The lab or stock center distributing the stocks in their collection.';


--
-- Name: COLUMN stockcollection.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stockcollection.type_id IS 'type_id is the collection type cv.';


--
-- Name: COLUMN stockcollection.contact_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stockcollection.contact_id IS 'contact_id links to the contact information for the collection.';


--
-- Name: COLUMN stockcollection.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stockcollection.name IS 'name is the collection.';


--
-- Name: COLUMN stockcollection.uniquename; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stockcollection.uniquename IS 'uniqename is the value of the collection cv.';


--
-- Name: stockcollection_stock; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stockcollection_stock (
    stockcollection_stock_id integer NOT NULL,
    stockcollection_id integer NOT NULL,
    stock_id integer NOT NULL
);


--
-- Name: TABLE stockcollection_stock; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stockcollection_stock IS 'stockcollection_stock links
a stock collection to the stocks which are contained in the collection.';


--
-- Name: stockcollection_stock_stockcollection_stock_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stockcollection_stock_stockcollection_stock_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stockcollection_stock_stockcollection_stock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stockcollection_stock_stockcollection_stock_id_seq OWNED BY public.stockcollection_stock.stockcollection_stock_id;


--
-- Name: stockcollection_stockcollection_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stockcollection_stockcollection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stockcollection_stockcollection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stockcollection_stockcollection_id_seq OWNED BY public.stockcollection.stockcollection_id;


--
-- Name: stockcollectionprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stockcollectionprop (
    stockcollectionprop_id integer NOT NULL,
    stockcollection_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE stockcollectionprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stockcollectionprop IS 'The table stockcollectionprop
contains the value of the stock collection such as website/email URLs;
the value of the stock collection order URLs.';


--
-- Name: COLUMN stockcollectionprop.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.stockcollectionprop.type_id IS 'The cv for the type_id is "stockcollection property type".';


--
-- Name: stockcollectionprop_stockcollectionprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stockcollectionprop_stockcollectionprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stockcollectionprop_stockcollectionprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stockcollectionprop_stockcollectionprop_id_seq OWNED BY public.stockcollectionprop.stockcollectionprop_id;


--
-- Name: stockprop; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stockprop (
    stockprop_id integer NOT NULL,
    stock_id integer NOT NULL,
    type_id integer NOT NULL,
    value text,
    rank integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE stockprop; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stockprop IS 'A stock can have any number of
slot-value property tags attached to it. This is an alternative to
hardcoding a list of columns in the relational schema, and is
completely extensible. There is a unique constraint, stockprop_c1, for
the combination of stock_id, rank, and type_id. Multivalued property-value pairs must be differentiated by rank.';


--
-- Name: stockprop_pub; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stockprop_pub (
    stockprop_pub_id integer NOT NULL,
    stockprop_id integer NOT NULL,
    pub_id integer NOT NULL
);


--
-- Name: TABLE stockprop_pub; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.stockprop_pub IS 'Provenance. Any stockprop assignment can optionally be supported by a publication.';


--
-- Name: stockprop_pub_stockprop_pub_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stockprop_pub_stockprop_pub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stockprop_pub_stockprop_pub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stockprop_pub_stockprop_pub_id_seq OWNED BY public.stockprop_pub.stockprop_pub_id;


--
-- Name: stockprop_stockprop_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stockprop_stockprop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stockprop_stockprop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stockprop_stockprop_id_seq OWNED BY public.stockprop.stockprop_id;


--
-- Name: synonym_synonym_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.synonym_synonym_id_seq
    START WITH 211782
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: synonym; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.synonym (
    synonym_id integer DEFAULT nextval('public.synonym_synonym_id_seq'::regclass) NOT NULL,
    name character varying(255) NOT NULL,
    type_id integer NOT NULL,
    synonym_sgml character varying(255) NOT NULL
);


--
-- Name: TABLE synonym; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.synonym IS 'A synonym for a feature. One feature can have multiple synonyms, and the same synonym can apply to multiple features.';


--
-- Name: COLUMN synonym.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.synonym.name IS 'The synonym itself. Should be human-readable machine-searchable ascii text.';


--
-- Name: COLUMN synonym.type_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.synonym.type_id IS 'Types would be symbol and fullname for now.';


--
-- Name: COLUMN synonym.synonym_sgml; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.synonym.synonym_sgml IS 'The fully specified synonym, with any non-ascii characters encoded in SGML.';


--
-- Name: tmp_cds_handler; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_cds_handler (
    cds_row_id integer NOT NULL,
    seq_id character varying(1024),
    gff_id character varying(1024),
    type character varying(1024) NOT NULL,
    fmin integer NOT NULL,
    fmax integer NOT NULL,
    object text NOT NULL
);


--
-- Name: tmp_cds_handler_cds_row_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tmp_cds_handler_cds_row_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tmp_cds_handler_cds_row_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tmp_cds_handler_cds_row_id_seq OWNED BY public.tmp_cds_handler.cds_row_id;


--
-- Name: tmp_cds_handler_relationship; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_cds_handler_relationship (
    rel_row_id integer NOT NULL,
    cds_row_id integer,
    parent_id character varying(1024),
    grandparent_id character varying(1024)
);


--
-- Name: tmp_cds_handler_relationship_rel_row_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tmp_cds_handler_relationship_rel_row_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tmp_cds_handler_relationship_rel_row_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tmp_cds_handler_relationship_rel_row_id_seq OWNED BY public.tmp_cds_handler_relationship.rel_row_id;


--
-- Name: tmp_gff_load_cache; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_gff_load_cache (
    feature_id integer,
    uniquename character varying(1000),
    type_id integer,
    organism_id integer
);


--
-- Name: tmp_intxn_evidence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_intxn_evidence (
    intxn_evidence_id smallint,
    definition character varying(254),
    count bigint,
    max double precision,
    min double precision,
    max_denserank bigint
);


--
-- Name: tmp_intxn_score; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_intxn_score (
    intxn_score_id bigint,
    gene1_id bigint,
    gene2_id bigint,
    intxn_evidence_id bigint,
    score double precision,
    rank bigint,
    denserank bigint
);


--
-- Name: tmp_locusmapping_fgenesh; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_locusmapping_fgenesh (
    iric_name character varying(255),
    fgenesh character varying(255)
);


--
-- Name: tmp_locusmapping_msu7; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_locusmapping_msu7 (
    iric_name character varying(255),
    msu7 character varying(255)
);


--
-- Name: tmp_locusmapping_rap; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_locusmapping_rap (
    iric_name character varying(255),
    rap character varying(255)
);


--
-- Name: tmp_promoter_1; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_promoter_1 (
    promoter_id bigint,
    name character varying(250),
    chrstr character varying(50),
    db bigint,
    type_id bigint,
    startpos numeric,
    endpos numeric,
    strand bigint,
    gene character varying(250),
    idstr character varying(250),
    note text,
    parent_id bigint,
    gene_overlaps character varying(1000),
    gene_id bigint
);


--
-- Name: tmp_qtl; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_qtl (
    name character varying(50),
    chromosome bigint,
    startpos bigint,
    endpos bigint,
    trait_name character varying(254),
    notes character varying(1024),
    db bigint,
    qtl_id bigint
);


--
-- Name: tmp_snpeff_raw_msu7; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tmp_snpeff_raw_msu7 (
    chrom integer,
    pos bigint,
    ref character varying(5),
    alt character varying(5),
    info character varying(2054),
    format character varying(254),
    sample_dummy character varying(254),
    id numeric NOT NULL
);


--
-- Name: v_allsample_basicprop; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_allsample_basicprop AS
 SELECT svs.sample_varietyset_id,
    ss.stock_sample_id,
    dx.accession AS assay,
    db.name AS dataset,
    svs.hdf5_index,
    subq.stock_id,
    subq.name,
    subq.ori_country,
    subq.subpopulation,
    subq.box_code,
    subq.gs_accession
   FROM ( SELECT final_result.stock_id,
            final_result.name,
            final_result.iris_unique_id,
            final_result.country_origin AS ori_country,
            final_result.subpopulation_v2 AS subpopulation,
            final_result.box_code,
            final_result.gs_accno AS gs_accession
           FROM public.crosstab('select irs.stock_id, ct.name cvterm, isp.value from stockprop isp, stock irs, cvterm ct  where irs.STOCK_ID=isp.STOCK_ID  and isp.type_id = ct.cvterm_id  and ct.name in (''country_origin'',''iris_unique_id'',''box_code'',''subpopulation_v2'',''accession number'' ) union select stock_id, ''name'' as  cvterm, name as value from stock order by 1,2'::text, 'select t.column_value from (select unnest(ARRAY[''name'', ''iris_unique_id'',''country_origin'',''subpopulation_v2'' , ''box_code'' , ''accession number'' ])column_value) t'::text) final_result(stock_id integer, name character varying(255), iris_unique_id character varying(4000), country_origin character varying(4000), subpopulation_v2 character varying(4000), box_code character varying(4000), gs_accno character varying(4000))) subq,
    public.db,
    public.dbxref dx,
    public.sample_varietyset svs,
    public.stock_sample ss
  WHERE ((db.db_id = svs.db_id) AND (svs.stock_sample_id = ss.stock_sample_id) AND (dx.dbxref_id = ss.dbxref_id) AND (ss.stock_id = subq.stock_id))
  ORDER BY db.name, svs.hdf5_index;


--
-- Name: v_allstock_basicprop; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_allstock_basicprop AS
 SELECT svs.sample_varietyset_id,
    ss.stock_sample_id,
    subq.stock_id,
    subq.name,
    dx.accession AS assay,
    subq.ori_country,
    subq.subpopulation,
    subq.box_code,
    subq.gs_accession,
    db.name AS dataset,
    ss.hdf5_index
   FROM ( SELECT final_result.stock_id,
            final_result.name,
            final_result.iris_unique_id,
            final_result.country_origin AS ori_country,
            final_result.subpopulation_v2 AS subpopulation,
            final_result.box_code,
            final_result.gs_accno AS gs_accession
           FROM public.crosstab('select irs.stock_id, ct.name cvterm, isp.value from stockprop isp, stock irs, cvterm ct  where irs.STOCK_ID=isp.STOCK_ID  and isp.type_id = ct.cvterm_id  and ct.name in (''country_origin'',''iris_unique_id'',''box_code'',''subpopulation_v2'',''accession number'' ) union select stock_id, ''name'' as  cvterm, name as value from stock order by 1,2'::text, 'select t.column_value from (select unnest(ARRAY[''name'', ''iris_unique_id'',''country_origin'',''subpopulation_v2'' , ''box_code'' , ''accession number'' ])column_value) t'::text) final_result(stock_id integer, name character varying(255), iris_unique_id character varying(4000), country_origin character varying(4000), subpopulation_v2 character varying(4000), box_code character varying(4000), gs_accno character varying(4000))) subq,
    public.db,
    public.sample_varietyset svs,
    public.dbxref dx,
    public.stock_sample ss
  WHERE ((db.db_id = svs.db_id) AND (svs.stock_sample_id = ss.stock_sample_id) AND (dx.dbxref_id = ss.dbxref_id) AND (ss.stock_id = subq.stock_id))
  ORDER BY db.name, svs.hdf5_index;


--
-- Name: v_co_variable_tms; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_co_variable_tms AS
 SELECT (split_part(final_result.subject_name, '::'::text, 1))::integer AS variable_id,
    final_result.subject_name AS variable_name,
    (split_part(final_result.ricetrait, '::'::text, 1))::integer AS trait_id,
    final_result.ricetrait AS trait,
    (split_part(final_result.ricemethod, '::'::text, 1))::integer AS method_id,
    final_result.ricemethod AS method,
    (split_part(final_result.ricescale, '::'::text, 1))::integer AS scale_id,
    final_result.ricescale AS scale
   FROM public.crosstab('select cts.cvterm_id || ''::''|| cts.name subject_name, split_part(cto.name,''::'',3) object_ns, cto.cvterm_id||''::''||cto.name as object_name from cvterm_relationship ctr, cvterm cts, cvterm cto, cvterm ctrel, cv cvrice 
where ctr.subject_id=cts.cvterm_id
and ctr.type_id=ctrel.cvterm_id
and ctr.object_id=cto.cvterm_id
and (cts.cv_id=cvrice.cv_id or cto.cv_id=cvrice.cv_id)
and cvrice.name=''Rice_ontology_allns''
and ctrel.name=''VARIABLE_OF'' order by 1,2'::text, 'select t.column_value from (select unnest(ARRAY[''RiceTrait'', ''RiceMethod'', ''RiceScale''])column_value) t'::text) final_result(subject_name text, ricetrait text, ricemethod text, ricescale text);


--
-- Name: v_cv_passport_allstocks; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_cv_passport_allstocks AS
 SELECT DISTINCT ct.cvterm_id,
    ct.name,
    ct.definition,
    db.name AS dataset
   FROM public.db,
    public.sample_varietyset dx,
    public.stock_sample ss,
    public.stockprop sp,
    public.cvterm ct,
    public.cv
  WHERE ((db.db_id = dx.db_id) AND (dx.stock_sample_id = ss.stock_sample_id) AND (ss.stock_id = sp.stock_id) AND (sp.type_id = ct.cvterm_id) AND (cv.cv_id = ct.cv_id) AND ((cv.name)::text = ANY (ARRAY['grims_stock_property'::text, 'rice_diversity_property'::text, 'multicrop_passport_ontology'::text])) AND (ct.is_obsolete = 0))
  ORDER BY ct.definition;


--
-- Name: v_cv_phenotype_vtms_allstocks; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_cv_phenotype_vtms_allstocks AS
 SELECT DISTINCT ct.cvterm_id,
    ct.name,
    ct.definition,
    db.name AS dataset,
    tms.trait,
    tms.method,
    tms.scale
   FROM public.db,
    public.sample_varietyset dx,
    public.stock_sample ss,
    public.stock_phenotype2 sp,
    (public.cvterm ct
     LEFT JOIN public.v_co_variable_tms tms ON ((tms.variable_id = ct.cvterm_id)))
  WHERE ((db.db_id = dx.db_id) AND (dx.stock_sample_id = ss.stock_sample_id) AND (ss.stock_id = sp.stock_id) AND (sp.type_id = ct.cvterm_id) AND (ct.is_obsolete = 0))
  ORDER BY ct.definition;


--
-- Name: v_cv_phenotype_allstocks; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_cv_phenotype_allstocks AS
 SELECT DISTINCT ct.cvterm_id,
        CASE
            WHEN ((vall.trait = ''::text) OR (vall.trait IS NULL)) THEN (vall.name)::text
            ELSE ((((split_part(vall.trait, '::'::text, 2) || '::'::text) || split_part(vall.trait, '::'::text, 3)) || '::'::text) || split_part(vall.trait, '::'::text, 4))
        END AS name,
    ct.definition,
    db.name AS dataset
   FROM public.db,
    public.sample_varietyset dx,
    public.stock_sample ss,
    public.stock_phenotype2 sp,
    (public.cvterm ct
     LEFT JOIN public.v_cv_phenotype_vtms_allstocks vall ON ((ct.cvterm_id = vall.cvterm_id)))
  WHERE ((db.db_id = dx.db_id) AND (dx.stock_sample_id = ss.stock_sample_id) AND (ss.stock_id = sp.stock_id) AND (sp.type_id = ct.cvterm_id) AND (ct.is_obsolete = 0))
  ORDER BY ct.definition;


--
-- Name: v_gene; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_gene AS
 SELECT g.gene_id,
    upper((g.name)::text) AS name,
    fsrc.uniquename AS chr,
    (fl.fmin + 1) AS fmin,
    fl.fmax,
    fl.strand,
    fl.phase,
    g.organism_id
   FROM public.gene g,
    public.featureloc fl,
    public.feature fsrc
  WHERE ((g.gene_id = fl.feature_id) AND (fl.srcfeature_id = fsrc.feature_id) AND (fsrc.organism_id = g.organism_id))
  ORDER BY (upper((g.name)::text));


--
-- Name: v_genotype_run; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_genotype_run AS
 SELECT r.genotype_run_id,
    r.date_performed,
    r.data_location,
    p.platform_id,
    v.variantset_id,
    v.name AS variantset,
    v.description AS vs_description,
    v.variant_type_id,
    db.db_id AS dataset_id,
    db.name AS dataset,
    db.description AS ds_description,
    ct.name AS method,
    cttype.name AS variant_type,
    r.visible,
    o.common_name
   FROM public.genotype_run r,
    public.variantset v,
    public.db,
    public.cvterm cttype,
    public.organism o,
    (public.platform p
     LEFT JOIN public.cvterm ct ON ((p.genotyping_method_id = ct.cvterm_id)))
  WHERE ((r.visible = true) AND (p.platform_id = r.platform_id) AND (p.db_id = db.db_id) AND (p.variantset_id = v.variantset_id) AND (cttype.cvterm_id = v.variant_type_id) AND (o.organism_id = v.organism_id));


--
-- Name: v_gwas_run; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_gwas_run AS
 SELECT gr.gwas_run_id,
    gr.trait_id,
    gt.name AS trait,
    gt.definition,
    s.gwas_subpopulation_id AS subpopulation_id,
    s.name AS subpopulation,
    ct.cvterm_id AS coterm_id,
    ct.name AS coterm,
    ct.definition AS codefinition,
    'default'::text AS method,
    NULL::date AS rundate,
    NULL::text AS qqplot
   FROM public.gwas_run gr,
    public.gwas_subpopulation s,
    (public.gwas_trait gt
     LEFT JOIN public.cvterm ct ON ((ct.cvterm_id = gt.phenotype_id)))
  WHERE ((gr.trait_id = gt.gwas_trait_id) AND (gr.subpopulation_id = s.gwas_subpopulation_id));


--
-- Name: v_indel_refposindex; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_indel_refposindex AS
 SELECT ifl.indel_feature_id,
    (ifl.srcfeature_id - 2) AS chromosome,
    (ifl."position" + 1) AS "position",
    ifl.refcall,
    9 AS organism_id,
    vvs.hdf5_index AS allele_index,
    vs.variantset_id AS type_id,
    vs.name AS variantset,
    NULL::text AS altcall,
    ifl.max_insert_len,
    ifl.max_delete_len
   FROM public.indel_feature if,
    public.indel_featureloc ifl,
    public.variant_variantset vvs,
    public.variantset vs
  WHERE ((ifl.indel_feature_id = if.indel_feature_id) AND (if.indel_feature_id = vvs.variant_feature_id) AND (vs.variantset_id = vvs.variantset_id) AND (vs.variant_type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'indel'::text))));


--
-- Name: v_locus_cvterm_cvtermpath; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_locus_cvterm_cvtermpath AS
 SELECT DISTINCT x.feature_id,
    x.name,
    x.fmin,
    x.fmax,
    x.strand,
    x.contig_id,
    x.contig_name,
    x.notes,
    x.cv_id,
    x.cv_name,
    x.db,
    x.cvterm_id,
    x.subj_acc,
    x.subj_cvterm,
    x.obj_acc,
    x.obj_cvterm,
    x.pathdistance,
    x.organism_id,
    x.common_name
   FROM ( SELECT f.feature_id,
            f.name,
            fl.fmin,
            fl.fmax,
            fl.strand,
            fsrc.feature_id AS contig_id,
            fsrc.uniquename AS contig_name,
            "substring"(f3.value, 1000) AS notes,
            c.cv_id,
            c.name AS cv_name,
            d.name AS db,
            cv.cvterm_id,
            db.accession AS subj_acc,
            cv.name AS subj_cvterm,
            db.accession AS obj_acc,
            cv.name AS obj_cvterm,
            0 AS pathdistance,
            f.organism_id,
            o.common_name
           FROM public.featureloc fl,
            public.feature_cvterm fc,
            public.cvterm cv,
            public.feature fsrc,
            public.organism o,
            public.dbxref db,
            public.db d,
            public.cv c,
            (public.feature f
             LEFT JOIN ( SELECT f2.feature_id,
                    fp.value
                   FROM public.featureprop fp,
                    public.feature f2
                  WHERE ((fp.feature_id = f2.feature_id) AND (fp.type_id IN ( SELECT cvterm.cvterm_id
                           FROM public.cvterm
                          WHERE ((cvterm.name)::text = 'Note'::text))))) f3 ON ((f.feature_id = f3.feature_id)))
          WHERE ((fc.feature_id = f.feature_id) AND (fc.cvterm_id = cv.cvterm_id) AND (fsrc.feature_id = fl.srcfeature_id) AND (fl.feature_id = f.feature_id) AND (f.organism_id = o.organism_id) AND (d.db_id = db.db_id) AND (c.cv_id = cv.cv_id) AND ((c.name)::text = ANY (ARRAY[('molecular_function'::character varying)::text, ('biological_process'::character varying)::text, ('cellular_component'::character varying)::text, ('plant_anatomy'::character varying)::text, ('plant_trait_ontology'::character varying)::text, ('quality'::character varying)::text, ('rice_trait'::character varying)::text, ('qtaro_gene_traits'::character varying)::text, ('plant_structure_development_stage'::character varying)::text])) AND (cv.dbxref_id = db.dbxref_id) AND (f.type_id IN ( SELECT cvterm.cvterm_id
                   FROM public.cvterm
                  WHERE ((cvterm.name)::text = 'gene'::text))))
        UNION
         SELECT f.feature_id,
            f.name,
            fl.fmin,
            fl.fmax,
            fl.strand,
            fsrc.feature_id AS contig_id,
            fsrc.uniquename AS contig_name,
            "substring"(f3.value, 1000) AS notes,
            c.cv_id,
            c.name AS cv_name,
            d.name AS db,
            cv_subj.cvterm_id,
            db_subj.accession AS subj_acc,
            cv_subj.name AS subj_cvterm,
            db_obj.accession AS obj_acc,
            cv_obj.name AS obj_cvterm,
            cvtp.pathdistance,
            f.organism_id,
            o.common_name
           FROM public.featureloc fl,
            public.feature_cvterm fc,
            public.cvterm cv_subj,
            public.feature fsrc,
            public.organism o,
            public.dbxref db_subj,
            public.cv c,
            public.db d,
            public.cvterm cv_obj,
            public.dbxref db_obj,
            public.cvtermpath cvtp,
            (public.feature f
             LEFT JOIN ( SELECT f2.feature_id,
                    fp.value
                   FROM public.featureprop fp,
                    public.feature f2
                  WHERE ((fp.feature_id = f2.feature_id) AND (fp.type_id IN ( SELECT cvterm.cvterm_id
                           FROM public.cvterm
                          WHERE ((cvterm.name)::text = 'Note'::text))))) f3 ON ((f.feature_id = f3.feature_id)))
          WHERE ((fc.feature_id = f.feature_id) AND (fsrc.feature_id = fl.srcfeature_id) AND (fl.feature_id = f.feature_id) AND (f.organism_id = o.organism_id) AND (fc.cvterm_id = cv_subj.cvterm_id) AND (c.cv_id = cv_subj.cv_id) AND ((c.name)::text = ANY (ARRAY[('molecular_function'::character varying)::text, ('biological_process'::character varying)::text, ('cellular_component'::character varying)::text, ('plant_anatomy'::character varying)::text, ('plant_trait_ontology'::character varying)::text, ('quality'::character varying)::text, ('rice_trait'::character varying)::text, ('qtaro_gene_traits'::character varying)::text, ('plant_structure_development_stage'::character varying)::text])) AND (cv_subj.dbxref_id = db_subj.dbxref_id) AND (cv_subj.cvterm_id = cvtp.subject_id) AND (cv_obj.cvterm_id = cvtp.object_id) AND (cvtp.pathdistance > '-1'::integer) AND (cv_obj.dbxref_id = db_obj.dbxref_id) AND (f.type_id IN ( SELECT cvterm.cvterm_id
                   FROM public.cvterm
                  WHERE ((cvterm.name)::text = 'gene'::text))) AND (d.db_id = db_obj.db_id))) x;


--
-- Name: v_locus_intxn_ricenetv2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_locus_intxn_ricenetv2 AS
 SELECT DISTINCT o.common_name,
    fsrc.feature_id AS contig_id,
    fsrc.uniquename AS contig_name,
    f.name,
    f.feature_id,
    fl.fmin,
    fl.fmax,
    fl.strand,
    o.organism_id,
    "substring"(fp.value, 1000) AS notes,
    fq.feature_id AS qfeature_id,
    fq.name AS qfeature_name,
    fint.score,
    fint.rank,
    fint.denserank,
    ev.max AS maxscore,
    ev.count AS maxrank
   FROM public.feature fq,
    public.tmp_intxn_score fint,
    public.tmp_intxn_evidence ev,
    public.organism o,
    public.feature fsrc,
    public.featureloc fl,
    (public.feature f
     LEFT JOIN public.featureprop fp ON (((f.feature_id = fp.feature_id) AND (fp.type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'Note'::text))))))
  WHERE ((((fint.gene1_id = fq.feature_id) AND (fint.gene2_id = f.feature_id)) OR ((fint.gene2_id = fq.feature_id) AND (fint.gene1_id = f.feature_id))) AND (fint.intxn_evidence_id = 1) AND (f.feature_id = fl.feature_id) AND (f.organism_id = o.organism_id) AND (fl.srcfeature_id = fsrc.feature_id) AND (ev.intxn_evidence_id = fint.intxn_evidence_id));


--
-- Name: v_locus_promoter; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_locus_promoter AS
 SELECT DISTINCT o.common_name,
    fsrc.feature_id AS contig_id,
    fsrc.uniquename AS contig_name,
    f.name,
    f.feature_id,
    fl.fmin,
    fl.fmax,
    fl.strand,
    o.organism_id,
    "substring"(pf.note, 1000) AS notes,
    pf.startpos AS pfeature_start,
    pf.endpos AS pfeature_end,
    pf.chrstr AS pfeature_chr,
    pf.promoter_id AS pfeature_id,
    pf.name AS pfeature_name,
    pf.db AS pfeature_db,
    ct.name AS pfeature_type,
    pf.gene_overlaps
   FROM public.feature f,
    public.feature fsrc,
    public.featureloc fl,
    public.tmp_promoter_1 pf,
    public.organism o,
    public.cvterm ct
  WHERE ((pf.gene_id = f.feature_id) AND (pf.type_id = ct.cvterm_id) AND (f.feature_id = fl.feature_id) AND (f.organism_id = o.organism_id) AND (fl.srcfeature_id = fsrc.feature_id));


--
-- Name: v_organism; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_organism AS
 SELECT organism.organism_id,
    organism.abbreviation,
    organism.genus,
    organism.species,
    organism.common_name,
    organism.comment
   FROM public.organism
  WHERE ((organism.common_name)::text = ANY (ARRAY[('Japonica nipponbare'::character varying)::text, ('MH63'::character varying)::text]))
  ORDER BY organism.organism_id;


--
-- Name: v_qtl; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_qtl AS
 SELECT tmp_qtl.qtl_id,
    tmp_qtl.name,
    tmp_qtl.chromosome,
    tmp_qtl.startpos,
    tmp_qtl.endpos,
    tmp_qtl.trait_name,
    tmp_qtl.notes,
    tmp_qtl.db AS db_id
   FROM public.tmp_qtl
  WHERE ((tmp_qtl.db = 1) OR ((tmp_qtl.db = 2) AND ((tmp_qtl.endpos - tmp_qtl.startpos) <= 100000)));


--
-- Name: v_scaffolds_organism; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_scaffolds_organism AS
 SELECT f.feature_id,
    f.name,
    f.uniquename,
    f.seqlen,
    f.organism_id,
    o.common_name,
    f.type_id,
    cv.name AS type
   FROM public.feature f,
    public.organism o,
    public.cvterm cv
  WHERE ((f.organism_id = o.organism_id) AND (f.type_id = cv.cvterm_id) AND ((cv.name)::text = ANY (ARRAY[('chromosome'::character varying)::text, ('contig'::character varying)::text, ('scaffold'::character varying)::text])));


--
-- Name: v_snp_refposindex_v2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_snp_refposindex_v2 AS
 SELECT sfl.snp_feature_id,
    (srcf.feature_id - 2) AS chromosome,
    (sfl."position" + 1) AS "position",
    sfl.refcall,
    ''::character varying(1) AS altcall,
    vvs.hdf5_index AS allele_index,
    v.variantset_id AS type_id,
    v.name AS variantset
   FROM public.snp_featureloc sfl,
    public.feature srcf,
    public.variant_variantset vvs,
    public.variantset v
  WHERE ((sfl.snp_feature_id = vvs.variant_feature_id) AND (sfl.srcfeature_id = srcf.feature_id) AND (vvs.variantset_id = v.variantset_id));


--
-- Name: v_snp_spliceacceptor_v2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_snp_spliceacceptor_v2 AS
 SELECT sfl.snp_feature_id,
    sfl.srcfeature_id,
    (sfl."position" + 1) AS "position",
    f.name AS chr,
    f.organism_id,
    v.name AS variantset
   FROM public.snp_featureloc sfl,
    public.feature f,
    public.snp_feature sf,
    public.variantset v,
    public.variant_variantset vs,
    public.snp_featureprop sfp
  WHERE ((sf.snp_feature_id = sfl.snp_feature_id) AND (sf.snp_feature_id = sfp.snp_feature_id) AND (f.feature_id = sfl.srcfeature_id) AND (sfp.type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'splice_acceptor_variant'::text))) AND (sf.snp_feature_id = vs.variant_feature_id) AND (vs.variantset_id = v.variantset_id) AND (v.variant_type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'SNP'::text))));


--
-- Name: v_snp_splicedonor_v2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_snp_splicedonor_v2 AS
 SELECT sfl.snp_feature_id,
    sfl.srcfeature_id,
    (sfl."position" + 1) AS "position",
    f.name AS chr,
    f.organism_id,
    v.name AS variantset
   FROM public.snp_featureloc sfl,
    public.feature f,
    public.snp_feature sf,
    public.variantset v,
    public.variant_variantset vs,
    public.snp_featureprop sfp
  WHERE ((sf.snp_feature_id = sfl.snp_feature_id) AND (sf.snp_feature_id = sfp.snp_feature_id) AND (f.feature_id = sfl.srcfeature_id) AND (sfp.type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'splice_donor_variant'::text))) AND (sf.snp_feature_id = vs.variant_feature_id) AND (vs.variantset_id = v.variantset_id) AND (v.variant_type_id IN ( SELECT cvterm.cvterm_id
           FROM public.cvterm
          WHERE ((cvterm.name)::text = 'SNP'::text))));


--
-- Name: v_snpeff; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_snpeff AS
 SELECT srp.snp_feature_id,
    tsf.chrom AS chromosome,
    tsf.pos AS "position",
    tsf.info AS annotation,
    '3kall'::character varying(255) AS variantset
   FROM public.v_snp_refposindex_v2 srp,
    public.tmp_snpeff_raw_msu7 tsf
  WHERE (((srp.variantset)::text = '3kall'::text) AND ((replace((srp.chromosome)::text, 'Chr'::text, ''::text))::integer = tsf.chrom) AND (tsf.pos = srp."position"));


--
-- Name: v_snpeff2; Type: MATERIALIZED VIEW; Schema: public; Owner: -
--

CREATE MATERIALIZED VIEW public.v_snpeff2 AS
 SELECT v_snpeff.snp_feature_id,
    v_snpeff.chromosome,
    v_snpeff."position",
    split_part((v_snpeff.annotation)::text, '|'::text, 2) AS annotation,
    v_snpeff.variantset
   FROM public.v_snpeff
  ORDER BY v_snpeff.variantset, v_snpeff.chromosome, v_snpeff."position"
  WITH NO DATA;


--
-- Name: v_stock_by_passport; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_by_passport AS
 SELECT sp.stockprop_id AS iric_stockprop_id,
    mv_stock_by_passport.stock_id AS iric_stock_id,
    mv_stock_by_passport.name,
    mv_stock_by_passport.assay AS iris_unique_id,
    mv_stock_by_passport.ori_country,
    mv_stock_by_passport.subpopulation,
    mv_stock_by_passport.gs_accession,
    mv_stock_by_passport.box_code,
    sp.value,
    sp.type_id,
    mv_stock_by_passport.dataset
   FROM public.v_allstock_basicprop mv_stock_by_passport,
    public.stockprop sp
  WHERE (sp.stock_id = mv_stock_by_passport.stock_id);


--
-- Name: v_stock_by_phenotype; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_by_phenotype AS
 SELECT sp.stock_phenotype2_id AS iric_stock_phenotype_id,
    mv_stock_by_phenotype.stock_id AS iric_stock_id,
    mv_stock_by_phenotype.name,
    mv_stock_by_phenotype.assay AS iris_unique_id,
    mv_stock_by_phenotype.ori_country,
    mv_stock_by_phenotype.subpopulation,
    mv_stock_by_phenotype.gs_accession,
    mv_stock_by_phenotype.box_code,
    sp.qual_value,
    sp.quan_value,
    sp.type_id AS phenotype_id,
    mv_stock_by_phenotype.dataset
   FROM public.v_allstock_basicprop mv_stock_by_phenotype,
    public.stock_phenotype2 sp
  WHERE (sp.stock_id = mv_stock_by_phenotype.stock_id);


--
-- Name: v_stock_passport; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_passport AS
 SELECT irp.stockprop_id,
    irp.stock_id,
    ct.name,
        CASE
            WHEN (ct.definition IS NOT NULL) THEN ct.definition
            ELSE (((dbct.name)::text || ':'::text) || (dxct.accession)::text)
        END AS definition,
    irp.value,
    db.name AS dataset
   FROM public.stockprop irp,
    public.cvterm ct,
    public.stock_sample ss,
    public.sample_varietyset dx,
    public.db,
    public.db dbct,
    public.dbxref dxct
  WHERE ((ss.stock_id = irp.stock_id) AND (irp.type_id = ct.cvterm_id) AND (ct.is_obsolete = 0) AND (ss.stock_sample_id = dx.stock_sample_id) AND (dx.db_id = db.db_id) AND (dxct.dbxref_id = ct.dbxref_id) AND (dxct.db_id = dbct.db_id))
  ORDER BY ct.definition;


--
-- Name: v_stock_phenotype; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype AS
 SELECT irp.stock_phenotype2_id,
    irp.stock_id,
    irp.type_id AS phenotype_id,
    ct.name,
    ct.definition,
    irp.quan_value,
    irp.qual_value,
    db.name AS dataset
   FROM public.stock_phenotype2 irp,
    public.cvterm ct,
    public.stock_sample ss,
    public.sample_varietyset dx,
    public.db
  WHERE ((ss.stock_id = irp.stock_id) AND (irp.type_id = ct.cvterm_id) AND (ct.is_obsolete = 0) AND (ss.stock_sample_id = dx.stock_sample_id) AND (dx.db_id = db.db_id));


--
-- Name: v_stock_phenotype2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype2 AS
 SELECT irp.stock_phenotype2_id,
    irp.stock_id,
    irp.type_id AS phenotype_id,
    ct.name,
    ct.definition,
    irp.quan_value,
    irp.qual_value,
    db.name AS dataset
   FROM public.stock_phenotype2 irp,
    public.cvterm ct,
    public.stock_sample ss,
    public.dbxref dx,
    public.db
  WHERE ((ss.stock_id = irp.stock_id) AND (irp.type_id = ct.cvterm_id) AND (ct.is_obsolete = 0) AND (ss.dbxref_id = dx.dbxref_id) AND (dx.db_id = db.db_id));


--
-- Name: v_stock_phenotype_co; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype_co AS
 SELECT irp.stock_phenotype2_id AS stock_phenotype_id,
    irp.stock_id,
    irp.phenotype_id,
    irp.name,
    irp.definition,
    irp.quan_value,
    irp.qual_value,
    irp.dataset,
    vall.variable_id,
    vall.variable_name,
    vall.trait_id,
    vall.trait,
    vall.method_id,
    vall.method,
    vall.scale_id,
    vall.scale
   FROM (public.v_stock_phenotype2 irp
     LEFT JOIN public.v_co_variable_tms vall ON ((irp.phenotype_id = vall.variable_id)));


--
-- Name: v_stock_phenotype_qualval; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype_qualval AS
 SELECT DISTINCT sp.qual_value,
    sp.type_id AS phenotype_id,
    db.name AS dataset
   FROM public.stock_phenotype2 sp,
    public.stock_sample ss,
    public.sample_varietyset dx,
    public.db
  WHERE ((sp.stock_id = ss.stock_id) AND (ss.stock_sample_id = dx.stock_sample_id) AND (dx.db_id = db.db_id))
  ORDER BY sp.qual_value;


--
-- Name: v_stock_phenotype_qualval2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype_qualval2 AS
 SELECT DISTINCT sp.qual_value,
    sp.type_id AS phenotype_id,
    db.name AS dataset
   FROM public.stock_phenotype2 sp,
    public.stock_sample ss,
    public.dbxref dx,
    public.db
  WHERE ((sp.stock_id = ss.stock_id) AND (ss.dbxref_id = dx.dbxref_id) AND (dx.db_id = db.db_id))
  ORDER BY sp.qual_value;


--
-- Name: v_stock_phenotype_quanval; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype_quanval AS
 SELECT DISTINCT round(sp.quan_value, 2) AS quan_value,
    sp.type_id AS phenotype_id,
    db.name AS dataset
   FROM public.stock_phenotype2 sp,
    public.stock_sample ss,
    public.sample_varietyset dx,
    public.db
  WHERE ((sp.stock_id = ss.stock_id) AND (ss.stock_sample_id = dx.stock_sample_id) AND (dx.db_id = db.db_id))
  ORDER BY (round(sp.quan_value, 2));


--
-- Name: v_stock_phenotype_quanval2; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_stock_phenotype_quanval2 AS
 SELECT DISTINCT round(sp.quan_value, 2) AS quan_value,
    sp.type_id AS phenotype_id,
    db.name AS dataset
   FROM public.stock_phenotype2 sp,
    public.stock_sample ss,
    public.dbxref dx,
    public.db
  WHERE ((sp.stock_id = ss.stock_id) AND (ss.dbxref_id = dx.dbxref_id) AND (dx.db_id = db.db_id))
  ORDER BY (round(sp.quan_value, 2));


--
-- Name: variant_variantset_variant_variantset_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.variant_variantset_variant_variantset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: variant_variantset_variant_variantset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.variant_variantset_variant_variantset_id_seq OWNED BY public.variant_variantset.variant_variantset_id;


--
-- Name: analysis analysis_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysis ALTER COLUMN analysis_id SET DEFAULT nextval('public.analysis_analysis_id_seq'::regclass);


--
-- Name: analysisfeature analysisfeature_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysisfeature ALTER COLUMN analysisfeature_id SET DEFAULT nextval('public.analysisfeature_analysisfeature_id_seq'::regclass);


--
-- Name: chadoprop chadoprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chadoprop ALTER COLUMN chadoprop_id SET DEFAULT nextval('public.chadoprop_chadoprop_id_seq'::regclass);


--
-- Name: cv cv_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cv ALTER COLUMN cv_id SET DEFAULT nextval('public.cv_cv_id_seq'::regclass);


--
-- Name: cvprop cvprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvprop ALTER COLUMN cvprop_id SET DEFAULT nextval('public.cvprop_cvprop_id_seq'::regclass);


--
-- Name: cvterm cvterm_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm ALTER COLUMN cvterm_id SET DEFAULT nextval('public.cvterm_cvterm_id_seq'::regclass);


--
-- Name: cvterm_dbxref cvterm_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_dbxref ALTER COLUMN cvterm_dbxref_id SET DEFAULT nextval('public.cvterm_dbxref_cvterm_dbxref_id_seq'::regclass);


--
-- Name: cvterm_relationship cvterm_relationship_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship ALTER COLUMN cvterm_relationship_id SET DEFAULT nextval('public.cvterm_relationship_cvterm_relationship_id_seq'::regclass);


--
-- Name: cvtermpath cvtermpath_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath ALTER COLUMN cvtermpath_id SET DEFAULT nextval('public.cvtermpath_cvtermpath_id_seq'::regclass);


--
-- Name: cvtermprop cvtermprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermprop ALTER COLUMN cvtermprop_id SET DEFAULT nextval('public.cvtermprop_cvtermprop_id_seq'::regclass);


--
-- Name: cvtermsynonym cvtermsynonym_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermsynonym ALTER COLUMN cvtermsynonym_id SET DEFAULT nextval('public.cvtermsynonym_cvtermsynonym_id_seq'::regclass);


--
-- Name: db db_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.db ALTER COLUMN db_id SET DEFAULT nextval('public.db_db_id_seq'::regclass);


--
-- Name: dbxref dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxref ALTER COLUMN dbxref_id SET DEFAULT nextval('public.dbxref_dbxref_id_seq'::regclass);


--
-- Name: dbxrefprop dbxrefprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxrefprop ALTER COLUMN dbxrefprop_id SET DEFAULT nextval('public.dbxrefprop_dbxrefprop_id_seq'::regclass);


--
-- Name: feature feature_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature ALTER COLUMN feature_id SET DEFAULT nextval('public.feature_feature_id_seq'::regclass);


--
-- Name: feature_cvterm feature_cvterm_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm ALTER COLUMN feature_cvterm_id SET DEFAULT nextval('public.feature_cvterm_feature_cvterm_id_seq'::regclass);


--
-- Name: feature_cvterm_dbxref feature_cvterm_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_dbxref ALTER COLUMN feature_cvterm_dbxref_id SET DEFAULT nextval('public.feature_cvterm_dbxref_feature_cvterm_dbxref_id_seq'::regclass);


--
-- Name: feature_cvterm_pub feature_cvterm_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_pub ALTER COLUMN feature_cvterm_pub_id SET DEFAULT nextval('public.feature_cvterm_pub_feature_cvterm_pub_id_seq'::regclass);


--
-- Name: feature_cvtermprop feature_cvtermprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvtermprop ALTER COLUMN feature_cvtermprop_id SET DEFAULT nextval('public.feature_cvtermprop_feature_cvtermprop_id_seq'::regclass);


--
-- Name: feature_dbxref feature_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_dbxref ALTER COLUMN feature_dbxref_id SET DEFAULT nextval('public.feature_dbxref_feature_dbxref_id_seq'::regclass);


--
-- Name: feature_expression feature_expression_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expression ALTER COLUMN feature_expression_id SET DEFAULT nextval('public.feature_expression_feature_expression_id_seq'::regclass);


--
-- Name: feature_expressionprop feature_expressionprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expressionprop ALTER COLUMN feature_expressionprop_id SET DEFAULT nextval('public.feature_expressionprop_feature_expressionprop_id_seq'::regclass);


--
-- Name: feature_genotype feature_genotype_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype ALTER COLUMN feature_genotype_id SET DEFAULT nextval('public.feature_genotype_feature_genotype_id_seq'::regclass);


--
-- Name: feature_phenotype feature_phenotype_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_phenotype ALTER COLUMN feature_phenotype_id SET DEFAULT nextval('public.feature_phenotype_feature_phenotype_id_seq'::regclass);


--
-- Name: feature_pub feature_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pub ALTER COLUMN feature_pub_id SET DEFAULT nextval('public.feature_pub_feature_pub_id_seq'::regclass);


--
-- Name: feature_pubprop feature_pubprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pubprop ALTER COLUMN feature_pubprop_id SET DEFAULT nextval('public.feature_pubprop_feature_pubprop_id_seq'::regclass);


--
-- Name: feature_relationship feature_relationship_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship ALTER COLUMN feature_relationship_id SET DEFAULT nextval('public.feature_relationship_feature_relationship_id_seq'::regclass);


--
-- Name: feature_relationship_pub feature_relationship_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship_pub ALTER COLUMN feature_relationship_pub_id SET DEFAULT nextval('public.feature_relationship_pub_feature_relationship_pub_id_seq'::regclass);


--
-- Name: feature_relationshipprop feature_relationshipprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop ALTER COLUMN feature_relationshipprop_id SET DEFAULT nextval('public.feature_relationshipprop_feature_relationshipprop_id_seq'::regclass);


--
-- Name: feature_relationshipprop_pub feature_relationshipprop_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop_pub ALTER COLUMN feature_relationshipprop_pub_id SET DEFAULT nextval('public.feature_relationshipprop_pub_feature_relationshipprop_pub_i_seq'::regclass);


--
-- Name: featureloc featureloc_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc ALTER COLUMN featureloc_id SET DEFAULT nextval('public.featureloc_featureloc_id_seq'::regclass);


--
-- Name: featureloc_pub featureloc_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc_pub ALTER COLUMN featureloc_pub_id SET DEFAULT nextval('public.featureloc_pub_featureloc_pub_id_seq'::regclass);


--
-- Name: featureprop featureprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop ALTER COLUMN featureprop_id SET DEFAULT nextval('public.featureprop_featureprop_id_seq'::regclass);


--
-- Name: featureprop_pub featureprop_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop_pub ALTER COLUMN featureprop_pub_id SET DEFAULT nextval('public.featureprop_pub_featureprop_pub_id_seq'::regclass);


--
-- Name: genotype_run genotype_run_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.genotype_run ALTER COLUMN genotype_run_id SET DEFAULT nextval('public.genotype_run_genotype_run_id_seq'::regclass);


--
-- Name: gff_sort_tmp row_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gff_sort_tmp ALTER COLUMN row_id SET DEFAULT nextval('public.gff_sort_tmp_row_id_seq'::regclass);


--
-- Name: indel_feature indel_feature_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_feature ALTER COLUMN indel_feature_id SET DEFAULT nextval('public.indel_feature_indel_feature_id_seq'::regclass);


--
-- Name: indel_featureloc indel_featureloc_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_featureloc ALTER COLUMN indel_featureloc_id SET DEFAULT nextval('public.indel_featureloc_indel_featureloc_id_seq'::regclass);


--
-- Name: materialized_view materialized_view_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.materialized_view ALTER COLUMN materialized_view_id SET DEFAULT nextval('public.materialized_view_materialized_view_id_seq'::regclass);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs ALTER COLUMN mv_convertpos_nb2allrefs_id SET DEFAULT nextval('public.mv_convertpos_nb2allrefs_mv_convertpos_nb2allrefs_id_seq'::regclass);


--
-- Name: organism organism_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism ALTER COLUMN organism_id SET DEFAULT nextval('public.organism_organism_id_seq'::regclass);


--
-- Name: organism_dbxref organism_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism_dbxref ALTER COLUMN organism_dbxref_id SET DEFAULT nextval('public.organism_dbxref_organism_dbxref_id_seq'::regclass);


--
-- Name: organismprop organismprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organismprop ALTER COLUMN organismprop_id SET DEFAULT nextval('public.organismprop_organismprop_id_seq'::regclass);


--
-- Name: platform platform_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform ALTER COLUMN platform_id SET DEFAULT nextval('public.platform_platform_id_seq1'::regclass);


--
-- Name: pub pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub ALTER COLUMN pub_id SET DEFAULT nextval('public.pub_pub_id_seq'::regclass);


--
-- Name: pub_dbxref pub_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_dbxref ALTER COLUMN pub_dbxref_id SET DEFAULT nextval('public.pub_dbxref_pub_dbxref_id_seq'::regclass);


--
-- Name: pub_relationship pub_relationship_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship ALTER COLUMN pub_relationship_id SET DEFAULT nextval('public.pub_relationship_pub_relationship_id_seq'::regclass);


--
-- Name: pubauthor pubauthor_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubauthor ALTER COLUMN pubauthor_id SET DEFAULT nextval('public.pubauthor_pubauthor_id_seq'::regclass);


--
-- Name: pubprop pubprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubprop ALTER COLUMN pubprop_id SET DEFAULT nextval('public.pubprop_pubprop_id_seq'::regclass);


--
-- Name: seed_order seed_order_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seed_order ALTER COLUMN seed_order_id SET DEFAULT nextval('public.seed_order_seed_order_id_seq'::regclass);


--
-- Name: snp_feature snp_feature_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_feature ALTER COLUMN snp_feature_id SET DEFAULT nextval('public.snp_feature_snp_feature_id_seq'::regclass);


--
-- Name: snp_featureloc snp_featureloc_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureloc ALTER COLUMN snp_featureloc_id SET DEFAULT nextval('public.snp_featureloc_snp_featureloc_id_seq'::regclass);


--
-- Name: snp_featureprop snp_featureprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureprop ALTER COLUMN snp_featureprop_id SET DEFAULT nextval('public.snp_featureprop_snp_featureprop_id_seq'::regclass);


--
-- Name: stock stock_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock ALTER COLUMN stock_id SET DEFAULT nextval('public.stock_stock_id_seq'::regclass);


--
-- Name: stock_cvterm stock_cvterm_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm ALTER COLUMN stock_cvterm_id SET DEFAULT nextval('public.stock_cvterm_stock_cvterm_id_seq'::regclass);


--
-- Name: stock_cvtermprop stock_cvtermprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvtermprop ALTER COLUMN stock_cvtermprop_id SET DEFAULT nextval('public.stock_cvtermprop_stock_cvtermprop_id_seq'::regclass);


--
-- Name: stock_dbxref stock_dbxref_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxref ALTER COLUMN stock_dbxref_id SET DEFAULT nextval('public.stock_dbxref_stock_dbxref_id_seq'::regclass);


--
-- Name: stock_dbxrefprop stock_dbxrefprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxrefprop ALTER COLUMN stock_dbxrefprop_id SET DEFAULT nextval('public.stock_dbxrefprop_stock_dbxrefprop_id_seq'::regclass);


--
-- Name: stock_genotype stock_genotype_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_genotype ALTER COLUMN stock_genotype_id SET DEFAULT nextval('public.stock_genotype_stock_genotype_id_seq'::regclass);


--
-- Name: stock_phenotype stock_phenotype_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype ALTER COLUMN stock_phenotype_id SET DEFAULT nextval('public.stock_phenotype_id_seq'::regclass);


--
-- Name: stock_pub stock_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_pub ALTER COLUMN stock_pub_id SET DEFAULT nextval('public.stock_pub_stock_pub_id_seq'::regclass);


--
-- Name: stock_relationship stock_relationship_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship ALTER COLUMN stock_relationship_id SET DEFAULT nextval('public.stock_relationship_stock_relationship_id_seq'::regclass);


--
-- Name: stock_relationship_cvterm stock_relationship_cvterm_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_cvterm ALTER COLUMN stock_relationship_cvterm_id SET DEFAULT nextval('public.stock_relationship_cvterm_stock_relationship_cvterm_id_seq'::regclass);


--
-- Name: stock_relationship_pub stock_relationship_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_pub ALTER COLUMN stock_relationship_pub_id SET DEFAULT nextval('public.stock_relationship_pub_stock_relationship_pub_id_seq'::regclass);


--
-- Name: stock_sample stock_sample_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_sample ALTER COLUMN stock_sample_id SET DEFAULT nextval('public.stock_sample_stock_sample_id_seq'::regclass);


--
-- Name: stockcollection stockcollection_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection ALTER COLUMN stockcollection_id SET DEFAULT nextval('public.stockcollection_stockcollection_id_seq'::regclass);


--
-- Name: stockcollection_stock stockcollection_stock_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection_stock ALTER COLUMN stockcollection_stock_id SET DEFAULT nextval('public.stockcollection_stock_stockcollection_stock_id_seq'::regclass);


--
-- Name: stockcollectionprop stockcollectionprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollectionprop ALTER COLUMN stockcollectionprop_id SET DEFAULT nextval('public.stockcollectionprop_stockcollectionprop_id_seq'::regclass);


--
-- Name: stockprop stockprop_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop ALTER COLUMN stockprop_id SET DEFAULT nextval('public.stockprop_stockprop_id_seq'::regclass);


--
-- Name: stockprop_pub stockprop_pub_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop_pub ALTER COLUMN stockprop_pub_id SET DEFAULT nextval('public.stockprop_pub_stockprop_pub_id_seq'::regclass);


--
-- Name: tmp_cds_handler cds_row_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_cds_handler ALTER COLUMN cds_row_id SET DEFAULT nextval('public.tmp_cds_handler_cds_row_id_seq'::regclass);


--
-- Name: tmp_cds_handler_relationship rel_row_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_cds_handler_relationship ALTER COLUMN rel_row_id SET DEFAULT nextval('public.tmp_cds_handler_relationship_rel_row_id_seq'::regclass);


--
-- Name: variant_variantset variant_variantset_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variant_variantset ALTER COLUMN variant_variantset_id SET DEFAULT nextval('public.variant_variantset_variant_variantset_id_seq'::regclass);


--
-- Name: analysis analysis_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_c1 UNIQUE (program, programversion, sourcename);


--
-- Name: analysis analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_pkey PRIMARY KEY (analysis_id);


--
-- Name: analysisfeature analysisfeature_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysisfeature
    ADD CONSTRAINT analysisfeature_c1 UNIQUE (feature_id, analysis_id);


--
-- Name: analysisfeature analysisfeature_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysisfeature
    ADD CONSTRAINT analysisfeature_pkey PRIMARY KEY (analysisfeature_id);


--
-- Name: chadoprop chadoprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chadoprop
    ADD CONSTRAINT chadoprop_c1 UNIQUE (type_id, rank);


--
-- Name: chadoprop chadoprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chadoprop
    ADD CONSTRAINT chadoprop_pkey PRIMARY KEY (chadoprop_id);


--
-- Name: cv cv_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cv
    ADD CONSTRAINT cv_c1 UNIQUE (name);


--
-- Name: cv cv_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cv
    ADD CONSTRAINT cv_pkey PRIMARY KEY (cv_id);


--
-- Name: cvprop cvprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvprop
    ADD CONSTRAINT cvprop_c1 UNIQUE (cv_id, type_id, rank);


--
-- Name: cvprop cvprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvprop
    ADD CONSTRAINT cvprop_pkey PRIMARY KEY (cvprop_id);


--
-- Name: cvterm cvterm_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm
    ADD CONSTRAINT cvterm_c1 UNIQUE (name, cv_id, is_obsolete);


--
-- Name: cvterm cvterm_cv_id_dbxref_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm
    ADD CONSTRAINT cvterm_cv_id_dbxref_id_key UNIQUE (cv_id, dbxref_id);


--
-- Name: cvterm_dbxref cvterm_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_dbxref
    ADD CONSTRAINT cvterm_dbxref_c1 UNIQUE (cvterm_id, dbxref_id);


--
-- Name: cvterm_dbxref cvterm_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_dbxref
    ADD CONSTRAINT cvterm_dbxref_pkey PRIMARY KEY (cvterm_dbxref_id);


--
-- Name: cvterm cvterm_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm
    ADD CONSTRAINT cvterm_pkey PRIMARY KEY (cvterm_id);


--
-- Name: cvterm_relationship cvterm_relationship_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship
    ADD CONSTRAINT cvterm_relationship_c1 UNIQUE (subject_id, object_id, type_id);


--
-- Name: cvterm_relationship cvterm_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship
    ADD CONSTRAINT cvterm_relationship_pkey PRIMARY KEY (cvterm_relationship_id);


--
-- Name: cvtermpath cvtermpath_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_c1 UNIQUE (subject_id, object_id, type_id, pathdistance);


--
-- Name: cvtermpath cvtermpath_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_pkey PRIMARY KEY (cvtermpath_id);


--
-- Name: cvtermprop cvtermprop_cvterm_id_type_id_value_rank_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermprop
    ADD CONSTRAINT cvtermprop_cvterm_id_type_id_value_rank_key UNIQUE (cvterm_id, type_id, value, rank);


--
-- Name: cvtermprop cvtermprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermprop
    ADD CONSTRAINT cvtermprop_pkey PRIMARY KEY (cvtermprop_id);


--
-- Name: cvtermsynonym cvtermsynonym_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermsynonym
    ADD CONSTRAINT cvtermsynonym_c1 UNIQUE (cvterm_id, synonym);


--
-- Name: cvtermsynonym cvtermsynonym_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermsynonym
    ADD CONSTRAINT cvtermsynonym_pkey PRIMARY KEY (cvtermsynonym_id);


--
-- Name: db db_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.db
    ADD CONSTRAINT db_c1 UNIQUE (name);


--
-- Name: db db_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.db
    ADD CONSTRAINT db_pkey PRIMARY KEY (db_id);


--
-- Name: dbxref dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxref
    ADD CONSTRAINT dbxref_c1 UNIQUE (db_id, accession, version);


--
-- Name: dbxref dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxref
    ADD CONSTRAINT dbxref_pkey PRIMARY KEY (dbxref_id);


--
-- Name: dbxrefprop dbxrefprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxrefprop
    ADD CONSTRAINT dbxrefprop_c1 UNIQUE (dbxref_id, type_id, rank);


--
-- Name: dbxrefprop dbxrefprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxrefprop
    ADD CONSTRAINT dbxrefprop_pkey PRIMARY KEY (dbxrefprop_id);


--
-- Name: feature feature_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_c1 UNIQUE (organism_id, uniquename, type_id);


--
-- Name: feature_cvterm feature_cvterm_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm
    ADD CONSTRAINT feature_cvterm_c1 UNIQUE (feature_id, cvterm_id, pub_id, rank);


--
-- Name: feature_cvterm_dbxref feature_cvterm_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_dbxref
    ADD CONSTRAINT feature_cvterm_dbxref_c1 UNIQUE (feature_cvterm_id, dbxref_id);


--
-- Name: feature_cvterm_dbxref feature_cvterm_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_dbxref
    ADD CONSTRAINT feature_cvterm_dbxref_pkey PRIMARY KEY (feature_cvterm_dbxref_id);


--
-- Name: feature_cvterm feature_cvterm_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm
    ADD CONSTRAINT feature_cvterm_pkey PRIMARY KEY (feature_cvterm_id);


--
-- Name: feature_cvterm_pub feature_cvterm_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_pub
    ADD CONSTRAINT feature_cvterm_pub_c1 UNIQUE (feature_cvterm_id, pub_id);


--
-- Name: feature_cvterm_pub feature_cvterm_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_pub
    ADD CONSTRAINT feature_cvterm_pub_pkey PRIMARY KEY (feature_cvterm_pub_id);


--
-- Name: feature_cvtermprop feature_cvtermprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvtermprop
    ADD CONSTRAINT feature_cvtermprop_c1 UNIQUE (feature_cvterm_id, type_id, rank);


--
-- Name: feature_cvtermprop feature_cvtermprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvtermprop
    ADD CONSTRAINT feature_cvtermprop_pkey PRIMARY KEY (feature_cvtermprop_id);


--
-- Name: feature_dbxref feature_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_dbxref
    ADD CONSTRAINT feature_dbxref_c1 UNIQUE (feature_id, dbxref_id);


--
-- Name: feature_dbxref feature_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_dbxref
    ADD CONSTRAINT feature_dbxref_pkey PRIMARY KEY (feature_dbxref_id);


--
-- Name: feature_expression feature_expression_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expression
    ADD CONSTRAINT feature_expression_c1 UNIQUE (expression_id, feature_id, pub_id);


--
-- Name: feature_expression feature_expression_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expression
    ADD CONSTRAINT feature_expression_pkey PRIMARY KEY (feature_expression_id);


--
-- Name: feature_expressionprop feature_expressionprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expressionprop
    ADD CONSTRAINT feature_expressionprop_c1 UNIQUE (feature_expression_id, type_id, rank);


--
-- Name: feature_expressionprop feature_expressionprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expressionprop
    ADD CONSTRAINT feature_expressionprop_pkey PRIMARY KEY (feature_expressionprop_id);


--
-- Name: feature_genotype feature_genotype_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype
    ADD CONSTRAINT feature_genotype_c1 UNIQUE (feature_id, genotype_id, cvterm_id, chromosome_id, rank, cgroup);


--
-- Name: feature_genotype feature_genotype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype
    ADD CONSTRAINT feature_genotype_pkey PRIMARY KEY (feature_genotype_id);


--
-- Name: feature_phenotype feature_phenotype_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_phenotype
    ADD CONSTRAINT feature_phenotype_c1 UNIQUE (feature_id, phenotype_id);


--
-- Name: feature_phenotype feature_phenotype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_phenotype
    ADD CONSTRAINT feature_phenotype_pkey PRIMARY KEY (feature_phenotype_id);


--
-- Name: feature feature_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_pkey PRIMARY KEY (feature_id);


--
-- Name: feature_pub feature_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pub
    ADD CONSTRAINT feature_pub_c1 UNIQUE (feature_id, pub_id);


--
-- Name: feature_pub feature_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pub
    ADD CONSTRAINT feature_pub_pkey PRIMARY KEY (feature_pub_id);


--
-- Name: feature_pubprop feature_pubprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pubprop
    ADD CONSTRAINT feature_pubprop_c1 UNIQUE (feature_pub_id, type_id, rank);


--
-- Name: feature_pubprop feature_pubprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pubprop
    ADD CONSTRAINT feature_pubprop_pkey PRIMARY KEY (feature_pubprop_id);


--
-- Name: feature_relationship feature_relationship_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship
    ADD CONSTRAINT feature_relationship_c1 UNIQUE (subject_id, object_id, type_id, rank);


--
-- Name: feature_relationship feature_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship
    ADD CONSTRAINT feature_relationship_pkey PRIMARY KEY (feature_relationship_id);


--
-- Name: feature_relationship_pub feature_relationship_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship_pub
    ADD CONSTRAINT feature_relationship_pub_c1 UNIQUE (feature_relationship_id, pub_id);


--
-- Name: feature_relationship_pub feature_relationship_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship_pub
    ADD CONSTRAINT feature_relationship_pub_pkey PRIMARY KEY (feature_relationship_pub_id);


--
-- Name: feature_relationshipprop feature_relationshipprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop
    ADD CONSTRAINT feature_relationshipprop_c1 UNIQUE (feature_relationship_id, type_id, rank);


--
-- Name: feature_relationshipprop feature_relationshipprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop
    ADD CONSTRAINT feature_relationshipprop_pkey PRIMARY KEY (feature_relationshipprop_id);


--
-- Name: feature_relationshipprop_pub feature_relationshipprop_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop_pub
    ADD CONSTRAINT feature_relationshipprop_pub_c1 UNIQUE (feature_relationshipprop_id, pub_id);


--
-- Name: feature_relationshipprop_pub feature_relationshipprop_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop_pub
    ADD CONSTRAINT feature_relationshipprop_pub_pkey PRIMARY KEY (feature_relationshipprop_pub_id);


--
-- Name: featureloc featureloc_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc
    ADD CONSTRAINT featureloc_c1 UNIQUE (feature_id, locgroup, rank);


--
-- Name: featureloc featureloc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc
    ADD CONSTRAINT featureloc_pkey PRIMARY KEY (featureloc_id);


--
-- Name: featureloc_pub featureloc_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc_pub
    ADD CONSTRAINT featureloc_pub_c1 UNIQUE (featureloc_id, pub_id);


--
-- Name: featureloc_pub featureloc_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc_pub
    ADD CONSTRAINT featureloc_pub_pkey PRIMARY KEY (featureloc_pub_id);


--
-- Name: featureprop featureprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop
    ADD CONSTRAINT featureprop_c1 UNIQUE (feature_id, type_id, rank);


--
-- Name: featureprop featureprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop
    ADD CONSTRAINT featureprop_pkey PRIMARY KEY (featureprop_id);


--
-- Name: featureprop_pub featureprop_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop_pub
    ADD CONSTRAINT featureprop_pub_c1 UNIQUE (featureprop_id, pub_id);


--
-- Name: featureprop_pub featureprop_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop_pub
    ADD CONSTRAINT featureprop_pub_pkey PRIMARY KEY (featureprop_pub_id);


--
-- Name: genotype_run genotype_run_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.genotype_run
    ADD CONSTRAINT genotype_run_pkey PRIMARY KEY (genotype_run_id);


--
-- Name: gff_sort_tmp gff_sort_tmp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gff_sort_tmp
    ADD CONSTRAINT gff_sort_tmp_pkey PRIMARY KEY (row_id);


--
-- Name: gwas_run gwas_run_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_run
    ADD CONSTRAINT gwas_run_pkey PRIMARY KEY (gwas_run_id);


--
-- Name: gwas_subpopulation gwas_subpopulation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_subpopulation
    ADD CONSTRAINT gwas_subpopulation_pkey PRIMARY KEY (gwas_subpopulation_id);


--
-- Name: gwas_trait gwas_trait_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_trait
    ADD CONSTRAINT gwas_trait_pkey PRIMARY KEY (gwas_trait_id);


--
-- Name: indel_feature indel_feature_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_feature
    ADD CONSTRAINT indel_feature_pkey PRIMARY KEY (indel_feature_id);


--
-- Name: indel_featureloc indel_featureloc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_featureloc
    ADD CONSTRAINT indel_featureloc_pkey PRIMARY KEY (indel_featureloc_id);


--
-- Name: materialized_view materialized_view_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.materialized_view
    ADD CONSTRAINT materialized_view_name_key UNIQUE (name);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_pkey PRIMARY KEY (mv_convertpos_nb2allrefs_id);


--
-- Name: organism organism_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism
    ADD CONSTRAINT organism_c1 UNIQUE (genus, species);


--
-- Name: organism_dbxref organism_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism_dbxref
    ADD CONSTRAINT organism_dbxref_c1 UNIQUE (organism_id, dbxref_id);


--
-- Name: organism_dbxref organism_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism_dbxref
    ADD CONSTRAINT organism_dbxref_pkey PRIMARY KEY (organism_dbxref_id);


--
-- Name: organism organism_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism
    ADD CONSTRAINT organism_pkey PRIMARY KEY (organism_id);


--
-- Name: organismprop organismprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organismprop
    ADD CONSTRAINT organismprop_c1 UNIQUE (organism_id, type_id, rank);


--
-- Name: organismprop organismprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organismprop
    ADD CONSTRAINT organismprop_pkey PRIMARY KEY (organismprop_id);


--
-- Name: platform platform_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT platform_pkey PRIMARY KEY (platform_id);


--
-- Name: pub pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub
    ADD CONSTRAINT pub_c1 UNIQUE (uniquename);


--
-- Name: pub_dbxref pub_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_dbxref
    ADD CONSTRAINT pub_dbxref_c1 UNIQUE (pub_id, dbxref_id);


--
-- Name: pub_dbxref pub_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_dbxref
    ADD CONSTRAINT pub_dbxref_pkey PRIMARY KEY (pub_dbxref_id);


--
-- Name: pub pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub
    ADD CONSTRAINT pub_pkey PRIMARY KEY (pub_id);


--
-- Name: pub_relationship pub_relationship_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship
    ADD CONSTRAINT pub_relationship_c1 UNIQUE (subject_id, object_id, type_id);


--
-- Name: pub_relationship pub_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship
    ADD CONSTRAINT pub_relationship_pkey PRIMARY KEY (pub_relationship_id);


--
-- Name: pubauthor pubauthor_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubauthor
    ADD CONSTRAINT pubauthor_c1 UNIQUE (pub_id, rank);


--
-- Name: pubauthor pubauthor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubauthor
    ADD CONSTRAINT pubauthor_pkey PRIMARY KEY (pubauthor_id);


--
-- Name: pubprop pubprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubprop
    ADD CONSTRAINT pubprop_c1 UNIQUE (pub_id, type_id, rank);


--
-- Name: pubprop pubprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubprop
    ADD CONSTRAINT pubprop_pkey PRIMARY KEY (pubprop_id);


--
-- Name: sample_varietyset sample_varietyset_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sample_varietyset
    ADD CONSTRAINT sample_varietyset_pkey PRIMARY KEY (sample_varietyset_id);


--
-- Name: seed_order seed_order_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.seed_order
    ADD CONSTRAINT seed_order_pkey PRIMARY KEY (seed_order_id);


--
-- Name: snp_feature snp_feature_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_feature
    ADD CONSTRAINT snp_feature_pkey PRIMARY KEY (snp_feature_id);


--
-- Name: snp_featureloc snp_featureloc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureloc
    ADD CONSTRAINT snp_featureloc_pkey PRIMARY KEY (snp_featureloc_id);


--
-- Name: snp_featureprop snp_featureprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureprop
    ADD CONSTRAINT snp_featureprop_pkey PRIMARY KEY (snp_featureprop_id);


--
-- Name: snp_featureprop snp_featureprop_snp_feature_id_type_id_value_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureprop
    ADD CONSTRAINT snp_featureprop_snp_feature_id_type_id_value_key UNIQUE (snp_feature_id, type_id, value);


--
-- Name: stock stock_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_c1 UNIQUE (organism_id, uniquename, type_id);


--
-- Name: stock_cvterm stock_cvterm_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm
    ADD CONSTRAINT stock_cvterm_c1 UNIQUE (stock_id, cvterm_id, pub_id, rank);


--
-- Name: stock_cvterm stock_cvterm_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm
    ADD CONSTRAINT stock_cvterm_pkey PRIMARY KEY (stock_cvterm_id);


--
-- Name: stock_cvtermprop stock_cvtermprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvtermprop
    ADD CONSTRAINT stock_cvtermprop_c1 UNIQUE (stock_cvterm_id, type_id, rank);


--
-- Name: stock_cvtermprop stock_cvtermprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvtermprop
    ADD CONSTRAINT stock_cvtermprop_pkey PRIMARY KEY (stock_cvtermprop_id);


--
-- Name: stock_dbxref stock_dbxref_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxref
    ADD CONSTRAINT stock_dbxref_c1 UNIQUE (stock_id, dbxref_id);


--
-- Name: stock_dbxref stock_dbxref_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxref
    ADD CONSTRAINT stock_dbxref_pkey PRIMARY KEY (stock_dbxref_id);


--
-- Name: stock_dbxrefprop stock_dbxrefprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxrefprop
    ADD CONSTRAINT stock_dbxrefprop_c1 UNIQUE (stock_dbxref_id, type_id, rank);


--
-- Name: stock_dbxrefprop stock_dbxrefprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxrefprop
    ADD CONSTRAINT stock_dbxrefprop_pkey PRIMARY KEY (stock_dbxrefprop_id);


--
-- Name: stock_genotype stock_genotype_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_genotype
    ADD CONSTRAINT stock_genotype_c1 UNIQUE (stock_id, genotype_id);


--
-- Name: stock_genotype stock_genotype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_genotype
    ADD CONSTRAINT stock_genotype_pkey PRIMARY KEY (stock_genotype_id);


--
-- Name: stock_phenotype2 stock_phenotype2_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype2
    ADD CONSTRAINT stock_phenotype2_pkey PRIMARY KEY (stock_phenotype2_id);


--
-- Name: stock_phenotype stock_phenotype_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype
    ADD CONSTRAINT stock_phenotype_pkey PRIMARY KEY (stock_phenotype_id);


--
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (stock_id);


--
-- Name: stock_pub stock_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_pub
    ADD CONSTRAINT stock_pub_c1 UNIQUE (stock_id, pub_id);


--
-- Name: stock_pub stock_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_pub
    ADD CONSTRAINT stock_pub_pkey PRIMARY KEY (stock_pub_id);


--
-- Name: stock_relationship stock_relationship_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship
    ADD CONSTRAINT stock_relationship_c1 UNIQUE (subject_id, object_id, type_id, rank);


--
-- Name: stock_relationship_cvterm stock_relationship_cvterm_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_cvterm
    ADD CONSTRAINT stock_relationship_cvterm_pkey PRIMARY KEY (stock_relationship_cvterm_id);


--
-- Name: stock_relationship stock_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship
    ADD CONSTRAINT stock_relationship_pkey PRIMARY KEY (stock_relationship_id);


--
-- Name: stock_relationship_pub stock_relationship_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_pub
    ADD CONSTRAINT stock_relationship_pub_c1 UNIQUE (stock_relationship_id, pub_id);


--
-- Name: stock_relationship_pub stock_relationship_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_pub
    ADD CONSTRAINT stock_relationship_pub_pkey PRIMARY KEY (stock_relationship_pub_id);


--
-- Name: stock_sample stock_sample_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_sample
    ADD CONSTRAINT stock_sample_pkey PRIMARY KEY (stock_sample_id);


--
-- Name: stockcollection stockcollection_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection
    ADD CONSTRAINT stockcollection_c1 UNIQUE (uniquename, type_id);


--
-- Name: stockcollection stockcollection_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection
    ADD CONSTRAINT stockcollection_pkey PRIMARY KEY (stockcollection_id);


--
-- Name: stockcollection_stock stockcollection_stock_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection_stock
    ADD CONSTRAINT stockcollection_stock_c1 UNIQUE (stockcollection_id, stock_id);


--
-- Name: stockcollection_stock stockcollection_stock_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection_stock
    ADD CONSTRAINT stockcollection_stock_pkey PRIMARY KEY (stockcollection_stock_id);


--
-- Name: stockcollectionprop stockcollectionprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollectionprop
    ADD CONSTRAINT stockcollectionprop_c1 UNIQUE (stockcollection_id, type_id, rank);


--
-- Name: stockcollectionprop stockcollectionprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollectionprop
    ADD CONSTRAINT stockcollectionprop_pkey PRIMARY KEY (stockcollectionprop_id);


--
-- Name: stockprop stockprop_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop
    ADD CONSTRAINT stockprop_c1 UNIQUE (stock_id, type_id, rank);


--
-- Name: stockprop stockprop_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop
    ADD CONSTRAINT stockprop_pkey PRIMARY KEY (stockprop_id);


--
-- Name: stockprop_pub stockprop_pub_c1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop_pub
    ADD CONSTRAINT stockprop_pub_c1 UNIQUE (stockprop_id, pub_id);


--
-- Name: stockprop_pub stockprop_pub_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop_pub
    ADD CONSTRAINT stockprop_pub_pkey PRIMARY KEY (stockprop_pub_id);


--
-- Name: synonym synonym_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.synonym
    ADD CONSTRAINT synonym_pkey PRIMARY KEY (synonym_id);


--
-- Name: tmp_cds_handler tmp_cds_handler_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_cds_handler
    ADD CONSTRAINT tmp_cds_handler_pkey PRIMARY KEY (cds_row_id);


--
-- Name: tmp_cds_handler_relationship tmp_cds_handler_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_cds_handler_relationship
    ADD CONSTRAINT tmp_cds_handler_relationship_pkey PRIMARY KEY (rel_row_id);


--
-- Name: tmp_snpeff_raw_msu7 tmp_snpeff_raw_msu7_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_snpeff_raw_msu7
    ADD CONSTRAINT tmp_snpeff_raw_msu7_pkey PRIMARY KEY (id);


--
-- Name: variant_variantset variant_variantset_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variant_variantset
    ADD CONSTRAINT variant_variantset_pkey PRIMARY KEY (variant_variantset_id);


--
-- Name: variant_variantset variant_variantset_variant_feature_id_variantset_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variant_variantset
    ADD CONSTRAINT variant_variantset_variant_feature_id_variantset_id_key UNIQUE (variant_feature_id, variantset_id);


--
-- Name: variantset variantset_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variantset
    ADD CONSTRAINT variantset_id_pkey PRIMARY KEY (variantset_id);


--
-- Name: analysisfeature_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX analysisfeature_idx1 ON public.analysisfeature USING btree (feature_id);


--
-- Name: analysisfeature_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX analysisfeature_idx2 ON public.analysisfeature USING btree (analysis_id);


--
-- Name: chrompos_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX chrompos_idx ON public.tmp_snpeff_raw_msu7 USING btree (chrom, pos);


--
-- Name: cv_cv_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cv_cv_id_idx ON public.cv USING btree (cv_id);


--
-- Name: cv_cv_id_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cv_cv_id_name_idx ON public.cv USING btree (cv_id, name);


--
-- Name: INDEX cvterm_c1; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON INDEX public.cvterm_c1 IS 'A name can mean different things in
different contexts; for example "chromosome" in SO and GO. A name
should be unique within an ontology or cv. A name may exist twice in a
cv, in both obsolete and non-obsolete forms - these will be for
different cvterms with different OBO identifiers; so GO documentation
for more details on obsoletion. Note that occasionally multiple
obsolete terms with the same name will exist in the same cv. If this
is a possibility for the ontology under consideration (e.g. GO) then the
ID should be appended to the name to ensure uniqueness.';


--
-- Name: cvterm_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_dbxref_idx1 ON public.cvterm_dbxref USING btree (cvterm_id);


--
-- Name: cvterm_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_dbxref_idx2 ON public.cvterm_dbxref USING btree (dbxref_id);


--
-- Name: cvterm_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_idx1 ON public.cvterm USING btree (cv_id);


--
-- Name: cvterm_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_idx2 ON public.cvterm USING btree (name);


--
-- Name: cvterm_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_idx3 ON public.cvterm USING btree (dbxref_id);


--
-- Name: cvterm_relationship_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_relationship_idx1 ON public.cvterm_relationship USING btree (type_id);


--
-- Name: cvterm_relationship_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_relationship_idx2 ON public.cvterm_relationship USING btree (subject_id);


--
-- Name: cvterm_relationship_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvterm_relationship_idx3 ON public.cvterm_relationship USING btree (object_id);


--
-- Name: cvtermpath_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermpath_idx1 ON public.cvtermpath USING btree (type_id);


--
-- Name: cvtermpath_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermpath_idx2 ON public.cvtermpath USING btree (subject_id);


--
-- Name: cvtermpath_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermpath_idx3 ON public.cvtermpath USING btree (object_id);


--
-- Name: cvtermpath_idx4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermpath_idx4 ON public.cvtermpath USING btree (cv_id);


--
-- Name: cvtermprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermprop_idx1 ON public.cvtermprop USING btree (cvterm_id);


--
-- Name: cvtermprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermprop_idx2 ON public.cvtermprop USING btree (type_id);


--
-- Name: cvtermsynonym_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX cvtermsynonym_idx1 ON public.cvtermsynonym USING btree (cvterm_id);


--
-- Name: dbxref_db; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dbxref_db ON public.dbxref USING btree (db_id);


--
-- Name: dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dbxref_idx2 ON public.dbxref USING btree (accession);


--
-- Name: dbxref_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dbxref_idx3 ON public.dbxref USING btree (version);


--
-- Name: dbxrefprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dbxrefprop_idx1 ON public.dbxrefprop USING btree (dbxref_id);


--
-- Name: dbxrefprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dbxrefprop_idx2 ON public.dbxrefprop USING btree (type_id);


--
-- Name: feature_cvterm_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_dbxref_idx1 ON public.feature_cvterm_dbxref USING btree (feature_cvterm_id);


--
-- Name: feature_cvterm_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_dbxref_idx2 ON public.feature_cvterm_dbxref USING btree (dbxref_id);


--
-- Name: feature_cvterm_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_idx1 ON public.feature_cvterm USING btree (feature_id);


--
-- Name: feature_cvterm_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_idx2 ON public.feature_cvterm USING btree (cvterm_id);


--
-- Name: feature_cvterm_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_idx3 ON public.feature_cvterm USING btree (pub_id);


--
-- Name: feature_cvterm_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_pub_idx1 ON public.feature_cvterm_pub USING btree (feature_cvterm_id);


--
-- Name: feature_cvterm_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvterm_pub_idx2 ON public.feature_cvterm_pub USING btree (pub_id);


--
-- Name: feature_cvtermprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvtermprop_idx1 ON public.feature_cvtermprop USING btree (feature_cvterm_id);


--
-- Name: feature_cvtermprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_cvtermprop_idx2 ON public.feature_cvtermprop USING btree (type_id);


--
-- Name: feature_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_dbxref_idx1 ON public.feature_dbxref USING btree (feature_id);


--
-- Name: feature_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_dbxref_idx2 ON public.feature_dbxref USING btree (dbxref_id);


--
-- Name: feature_expression_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_expression_idx1 ON public.feature_expression USING btree (expression_id);


--
-- Name: feature_expression_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_expression_idx2 ON public.feature_expression USING btree (feature_id);


--
-- Name: feature_expression_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_expression_idx3 ON public.feature_expression USING btree (pub_id);


--
-- Name: feature_expressionprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_expressionprop_idx1 ON public.feature_expressionprop USING btree (feature_expression_id);


--
-- Name: feature_expressionprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_expressionprop_idx2 ON public.feature_expressionprop USING btree (type_id);


--
-- Name: feature_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_feature_id_idx ON public.feature USING btree (feature_id);


--
-- Name: feature_feature_id_name_organism_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_feature_id_name_organism_id_idx ON public.feature USING btree (feature_id, name, organism_id);


--
-- Name: feature_feature_id_uniquename_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_feature_id_uniquename_idx ON public.feature USING btree (feature_id, uniquename);


--
-- Name: feature_feature_id_uniquename_organism_id_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_feature_id_uniquename_organism_id_type_id_idx ON public.feature USING btree (feature_id, uniquename, organism_id, type_id);


--
-- Name: feature_genotype_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_genotype_idx1 ON public.feature_genotype USING btree (feature_id);


--
-- Name: feature_genotype_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_genotype_idx2 ON public.feature_genotype USING btree (genotype_id);


--
-- Name: feature_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_idx1 ON public.feature USING btree (dbxref_id);


--
-- Name: feature_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_idx2 ON public.feature USING btree (organism_id);


--
-- Name: feature_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_idx3 ON public.feature USING btree (type_id);


--
-- Name: feature_idx4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_idx4 ON public.feature USING btree (uniquename);


--
-- Name: feature_idx5; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_idx5 ON public.feature USING btree (lower((name)::text));


--
-- Name: feature_name_ind1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_ind1 ON public.feature USING btree (name);


--
-- Name: feature_name_lower_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_lower_idx ON public.feature USING btree (lower((name)::text) varchar_pattern_ops);


--
-- Name: feature_name_lower_txt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_lower_txt_idx ON public.feature USING btree (lower((name)::text) text_pattern_ops);


--
-- Name: feature_name_lower_vc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_lower_vc_idx ON public.feature USING btree (name varchar_pattern_ops);


--
-- Name: feature_name_organism_id_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_organism_id_type_id_idx ON public.feature USING btree (name, organism_id, type_id);


--
-- Name: feature_name_txt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_txt_idx ON public.feature USING btree (name text_pattern_ops);


--
-- Name: feature_name_upper_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_upper_idx ON public.feature USING btree (upper((name)::text) varchar_pattern_ops);


--
-- Name: feature_name_upper_txt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_name_upper_txt_idx ON public.feature USING btree (upper((name)::text) text_pattern_ops);


--
-- Name: feature_orgtypename_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_orgtypename_idx ON public.feature USING btree (organism_id, type_id, name);


--
-- Name: feature_phenotype_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_phenotype_idx1 ON public.feature_phenotype USING btree (feature_id);


--
-- Name: feature_phenotype_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_phenotype_idx2 ON public.feature_phenotype USING btree (phenotype_id);


--
-- Name: feature_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_pub_idx1 ON public.feature_pub USING btree (feature_id);


--
-- Name: feature_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_pub_idx2 ON public.feature_pub USING btree (pub_id);


--
-- Name: feature_pubprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_pubprop_idx1 ON public.feature_pubprop USING btree (feature_pub_id);


--
-- Name: feature_relationship_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationship_idx1 ON public.feature_relationship USING btree (subject_id);


--
-- Name: feature_relationship_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationship_idx2 ON public.feature_relationship USING btree (object_id);


--
-- Name: feature_relationship_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationship_idx3 ON public.feature_relationship USING btree (type_id);


--
-- Name: feature_relationship_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationship_pub_idx1 ON public.feature_relationship_pub USING btree (feature_relationship_id);


--
-- Name: feature_relationship_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationship_pub_idx2 ON public.feature_relationship_pub USING btree (pub_id);


--
-- Name: feature_relationshipprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationshipprop_idx1 ON public.feature_relationshipprop USING btree (feature_relationship_id);


--
-- Name: feature_relationshipprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationshipprop_idx2 ON public.feature_relationshipprop USING btree (type_id);


--
-- Name: feature_relationshipprop_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationshipprop_pub_idx1 ON public.feature_relationshipprop_pub USING btree (feature_relationshipprop_id);


--
-- Name: feature_relationshipprop_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX feature_relationshipprop_pub_idx2 ON public.feature_relationshipprop_pub USING btree (pub_id);


--
-- Name: featureloc_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureloc_idx1 ON public.featureloc USING btree (feature_id);


--
-- Name: featureloc_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureloc_idx2 ON public.featureloc USING btree (srcfeature_id);


--
-- Name: featureloc_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureloc_idx3 ON public.featureloc USING btree (srcfeature_id, fmin, fmax);


--
-- Name: featureloc_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureloc_pub_idx1 ON public.featureloc_pub USING btree (featureloc_id);


--
-- Name: featureloc_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureloc_pub_idx2 ON public.featureloc_pub USING btree (pub_id);


--
-- Name: featureprop_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_feature_id_idx ON public.featureprop USING btree (feature_id);


--
-- Name: featureprop_feature_id_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_feature_id_type_id_idx ON public.featureprop USING btree (feature_id, type_id);


--
-- Name: featureprop_feature_id_value_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_feature_id_value_idx ON public.featureprop USING btree (feature_id, value);


--
-- Name: featureprop_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_pub_idx1 ON public.featureprop_pub USING btree (featureprop_id);


--
-- Name: featureprop_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_pub_idx2 ON public.featureprop_pub USING btree (pub_id);


--
-- Name: featureprop_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX featureprop_type_id_idx ON public.featureprop USING btree (type_id);


--
-- Name: fki_synonym_cvterm_fk; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_synonym_cvterm_fk ON public.synonym USING btree (type_id);


--
-- Name: gff_sort_tmp_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gff_sort_tmp_idx1 ON public.gff_sort_tmp USING btree (refseq);


--
-- Name: gff_sort_tmp_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gff_sort_tmp_idx2 ON public.gff_sort_tmp USING btree (id);


--
-- Name: gff_sort_tmp_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gff_sort_tmp_idx3 ON public.gff_sort_tmp USING btree (parent);


--
-- Name: idx_intxn_score1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_intxn_score1 ON public.tmp_intxn_score USING btree (gene1_id, gene2_id, intxn_evidence_id);


--
-- Name: idx_promoter1_3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_promoter1_3 ON public.tmp_promoter_1 USING btree (idstr, db, type_id);


--
-- Name: idx_promoter_gene; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_promoter_gene ON public.tmp_promoter_1 USING btree (gene);


--
-- Name: idx_tmp_prom2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tmp_prom2 ON public.tmp_promoter_1 USING btree (db);


--
-- Name: idx_tmp_promoter1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tmp_promoter1 ON public.tmp_promoter_1 USING btree (gene, promoter_id);


--
-- Name: indel_featureloc_indel_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX indel_featureloc_indel_feature_id_idx ON public.indel_featureloc USING btree (indel_feature_id);


--
-- Name: indel_featureloc_srcfeature_id_position_organism_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX indel_featureloc_srcfeature_id_position_organism_id_idx ON public.indel_featureloc USING btree (srcfeature_id, "position", organism_id);


--
-- Name: lm_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX lm_name_idx ON public.locus_mapping USING btree (name);


--
-- Name: locus_mapping_feature_feature_id_db_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX locus_mapping_feature_feature_id_db_id_idx ON public.locus_mapping_feature USING btree (feature_id, db_id);


--
-- Name: locus_mapping_feature_locus_mapping_id_db_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX locus_mapping_feature_locus_mapping_id_db_id_idx ON public.locus_mapping_feature USING btree (locus_mapping_id, db_id);


--
-- Name: msu7_patops_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX msu7_patops_idx ON public.locus_mapping USING btree (msu7 varchar_pattern_ops);


--
-- Name: name_patops_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX name_patops_idx ON public.locus_mapping USING btree (name varchar_pattern_ops);


--
-- Name: organism_common_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX organism_common_name_idx ON public.organism USING btree (common_name);


--
-- Name: organism_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX organism_dbxref_idx1 ON public.organism_dbxref USING btree (organism_id);


--
-- Name: organism_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX organism_dbxref_idx2 ON public.organism_dbxref USING btree (dbxref_id);


--
-- Name: organismprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX organismprop_idx1 ON public.organismprop USING btree (organism_id);


--
-- Name: organismprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX organismprop_idx2 ON public.organismprop USING btree (type_id);


--
-- Name: pub_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_dbxref_idx1 ON public.pub_dbxref USING btree (pub_id);


--
-- Name: pub_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_dbxref_idx2 ON public.pub_dbxref USING btree (dbxref_id);


--
-- Name: pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_idx1 ON public.pub USING btree (type_id);


--
-- Name: pub_relationship_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_relationship_idx1 ON public.pub_relationship USING btree (subject_id);


--
-- Name: pub_relationship_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_relationship_idx2 ON public.pub_relationship USING btree (object_id);


--
-- Name: pub_relationship_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pub_relationship_idx3 ON public.pub_relationship USING btree (type_id);


--
-- Name: pubauthor_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pubauthor_idx2 ON public.pubauthor USING btree (pub_id);


--
-- Name: pubprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pubprop_idx1 ON public.pubprop USING btree (pub_id);


--
-- Name: pubprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pubprop_idx2 ON public.pubprop USING btree (type_id);


--
-- Name: rappred_patops_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rappred_patops_idx ON public.locus_mapping USING btree (rap_predicted varchar_pattern_ops);


--
-- Name: raprep_patops_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX raprep_patops_idx ON public.locus_mapping USING btree (rap_representative varchar_pattern_ops);


--
-- Name: snp_feature_variantset_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_feature_variantset_id_idx ON public.snp_feature USING btree (variantset_id);


--
-- Name: snp_featureloc_organism_id_srcfeature_id_position_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_featureloc_organism_id_srcfeature_id_position_idx ON public.snp_featureloc USING btree (organism_id, srcfeature_id, "position");


--
-- Name: snp_featureloc_snp_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_featureloc_snp_feature_id_idx ON public.snp_featureloc USING btree (snp_feature_id);


--
-- Name: snp_featureloc_srcfeature_id_position_organism_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_featureloc_srcfeature_id_position_organism_id_idx ON public.snp_featureloc USING btree (srcfeature_id, "position", organism_id);


--
-- Name: snp_featureprop_snp_feature_id_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_featureprop_snp_feature_id_type_id_idx ON public.snp_featureprop USING btree (snp_feature_id, type_id);


--
-- Name: snp_featureprop_type_id_snp_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_featureprop_type_id_snp_feature_id_idx ON public.snp_featureprop USING btree (type_id, snp_feature_id);


--
-- Name: snp_genotype_3_snp_feature_id_genotype_run_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_genotype_3_snp_feature_id_genotype_run_id_idx ON public.snp_genotype USING btree (snp_feature_id, genotype_run_id);


--
-- Name: snp_genotype_snp_feature_id_genotype_run_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX snp_genotype_snp_feature_id_genotype_run_id_idx ON public."snp_genotype_wis9100Mhdra" USING btree (snp_feature_id, genotype_run_id);


--
-- Name: stock_cvterm_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_cvterm_idx1 ON public.stock_cvterm USING btree (stock_id);


--
-- Name: stock_cvterm_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_cvterm_idx2 ON public.stock_cvterm USING btree (cvterm_id);


--
-- Name: stock_cvterm_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_cvterm_idx3 ON public.stock_cvterm USING btree (pub_id);


--
-- Name: stock_cvtermprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_cvtermprop_idx1 ON public.stock_cvtermprop USING btree (stock_cvterm_id);


--
-- Name: stock_cvtermprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_cvtermprop_idx2 ON public.stock_cvtermprop USING btree (type_id);


--
-- Name: stock_dbxref_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_dbxref_idx1 ON public.stock_dbxref USING btree (stock_id);


--
-- Name: stock_dbxref_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_dbxref_idx2 ON public.stock_dbxref USING btree (dbxref_id);


--
-- Name: stock_dbxrefprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_dbxrefprop_idx1 ON public.stock_dbxrefprop USING btree (stock_dbxref_id);


--
-- Name: stock_dbxrefprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_dbxrefprop_idx2 ON public.stock_dbxrefprop USING btree (type_id);


--
-- Name: stock_genotype_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_genotype_idx1 ON public.stock_genotype USING btree (stock_id);


--
-- Name: stock_genotype_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_genotype_idx2 ON public.stock_genotype USING btree (genotype_id);


--
-- Name: stock_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_idx1 ON public.stock USING btree (dbxref_id);


--
-- Name: stock_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_idx2 ON public.stock USING btree (organism_id);


--
-- Name: stock_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_idx3 ON public.stock USING btree (type_id);


--
-- Name: stock_idx4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_idx4 ON public.stock USING btree (uniquename);


--
-- Name: stock_name_ind1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_name_ind1 ON public.stock USING btree (name);


--
-- Name: stock_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_pub_idx1 ON public.stock_pub USING btree (stock_id);


--
-- Name: stock_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_pub_idx2 ON public.stock_pub USING btree (pub_id);


--
-- Name: stock_relationship_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_relationship_idx1 ON public.stock_relationship USING btree (subject_id);


--
-- Name: stock_relationship_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_relationship_idx2 ON public.stock_relationship USING btree (object_id);


--
-- Name: stock_relationship_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_relationship_idx3 ON public.stock_relationship USING btree (type_id);


--
-- Name: stock_relationship_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_relationship_pub_idx1 ON public.stock_relationship_pub USING btree (stock_relationship_id);


--
-- Name: stock_relationship_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_relationship_pub_idx2 ON public.stock_relationship_pub USING btree (pub_id);


--
-- Name: stock_sample_dbxref_id_hdf5_index_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_dbxref_id_hdf5_index_idx ON public.stock_sample USING btree (dbxref_id, hdf5_index);


--
-- Name: stock_sample_dbxref_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_dbxref_id_idx ON public.stock_sample USING btree (dbxref_id);


--
-- Name: stock_sample_dbxref_id_tmp_oldstock_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_dbxref_id_tmp_oldstock_id_idx ON public.stock_sample USING btree (dbxref_id, tmp_oldstock_id);


--
-- Name: stock_sample_hdf5_index_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_hdf5_index_idx ON public.stock_sample USING btree (hdf5_index);


--
-- Name: stock_sample_stock_id_dbxref_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_stock_id_dbxref_id_idx ON public.stock_sample USING btree (stock_id, dbxref_id);


--
-- Name: stock_sample_tmp_oldstock_id_dbxref_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_tmp_oldstock_id_dbxref_id_idx ON public.stock_sample USING btree (tmp_oldstock_id, dbxref_id);


--
-- Name: stock_sample_tmp_oldstock_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_sample_tmp_oldstock_id_idx ON public.stock_sample USING btree (tmp_oldstock_id);


--
-- Name: stock_stock_id_tmp_oldstock_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_stock_id_tmp_oldstock_id_idx ON public.stock USING btree (stock_id, tmp_oldstock_id);


--
-- Name: stock_uniquename_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_uniquename_idx ON public.stock USING btree (uniquename varchar_pattern_ops);


--
-- Name: stock_uniquename_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stock_uniquename_idx1 ON public.stock USING btree (uniquename);


--
-- Name: stockcollection_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_idx1 ON public.stockcollection USING btree (contact_id);


--
-- Name: stockcollection_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_idx2 ON public.stockcollection USING btree (type_id);


--
-- Name: stockcollection_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_idx3 ON public.stockcollection USING btree (uniquename);


--
-- Name: stockcollection_name_ind1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_name_ind1 ON public.stockcollection USING btree (name);


--
-- Name: stockcollection_stock_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_stock_idx1 ON public.stockcollection_stock USING btree (stockcollection_id);


--
-- Name: stockcollection_stock_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollection_stock_idx2 ON public.stockcollection_stock USING btree (stock_id);


--
-- Name: stockcollectionprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollectionprop_idx1 ON public.stockcollectionprop USING btree (stockcollection_id);


--
-- Name: stockcollectionprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockcollectionprop_idx2 ON public.stockcollectionprop USING btree (type_id);


--
-- Name: stockprop_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockprop_idx1 ON public.stockprop USING btree (stock_id);


--
-- Name: stockprop_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockprop_idx2 ON public.stockprop USING btree (type_id);


--
-- Name: stockprop_pub_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockprop_pub_idx1 ON public.stockprop_pub USING btree (stockprop_id);


--
-- Name: stockprop_pub_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX stockprop_pub_idx2 ON public.stockprop_pub USING btree (pub_id);


--
-- Name: synonym_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX synonym_name_idx ON public.synonym USING btree (name);


--
-- Name: synonym_synonym_sgml_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX synonym_synonym_sgml_idx ON public.synonym USING btree (synonym_sgml);


--
-- Name: tmp_cds_handler_fmax; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_cds_handler_fmax ON public.tmp_cds_handler USING btree (fmax);


--
-- Name: tmp_cds_handler_relationship_grandparent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_cds_handler_relationship_grandparent ON public.tmp_cds_handler_relationship USING btree (grandparent_id);


--
-- Name: tmp_cds_handler_seq_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_cds_handler_seq_id ON public.tmp_cds_handler USING btree (seq_id);


--
-- Name: tmp_gff_load_cache_idx1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_gff_load_cache_idx1 ON public.tmp_gff_load_cache USING btree (feature_id);


--
-- Name: tmp_gff_load_cache_idx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_gff_load_cache_idx2 ON public.tmp_gff_load_cache USING btree (uniquename);


--
-- Name: tmp_gff_load_cache_idx3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_gff_load_cache_idx3 ON public.tmp_gff_load_cache USING btree (uniquename, type_id, organism_id);


--
-- Name: tmp_intxn_score_gene1_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_intxn_score_gene1_id_idx ON public.tmp_intxn_score USING btree (gene1_id);


--
-- Name: tmp_intxn_score_gene2_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_intxn_score_gene2_id_idx ON public.tmp_intxn_score USING btree (gene2_id);


--
-- Name: tmp_intxn_score_intxn_evidence_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_intxn_score_intxn_evidence_id_idx ON public.tmp_intxn_score USING btree (intxn_evidence_id);


--
-- Name: tmp_locusmapping_fgenesg_fgenesh_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_fgenesg_fgenesh_idx ON public.tmp_locusmapping_fgenesh USING btree (fgenesh);


--
-- Name: tmp_locusmapping_fgenesh_iric_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_fgenesh_iric_name_idx ON public.tmp_locusmapping_fgenesh USING btree (iric_name);


--
-- Name: tmp_locusmapping_msu7_iric_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_msu7_iric_name_idx ON public.tmp_locusmapping_msu7 USING btree (iric_name);


--
-- Name: tmp_locusmapping_msu7_msu7_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_msu7_msu7_idx ON public.tmp_locusmapping_msu7 USING btree (msu7);


--
-- Name: tmp_locusmapping_rap_iric_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_rap_iric_name_idx ON public.tmp_locusmapping_rap USING btree (iric_name);


--
-- Name: tmp_locusmapping_rap_rap_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_locusmapping_rap_rap_idx ON public.tmp_locusmapping_rap USING btree (rap);


--
-- Name: tmp_qtl_index1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_qtl_index1 ON public.tmp_qtl USING btree (chromosome, startpos, endpos, db);


--
-- Name: tmp_qtl_index2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_qtl_index2 ON public.tmp_qtl USING btree (name);


--
-- Name: tmp_qtl_index3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_qtl_index3 ON public.tmp_qtl USING btree (trait_name);


--
-- Name: tmp_qtl_index5; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tmp_qtl_index5 ON public.tmp_qtl USING btree (chromosome, startpos, endpos);


--
-- Name: v_chrompos_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX v_chrompos_idx ON public.v_snpeff2 USING btree (chromosome, "position");


--
-- Name: variant_variantset_variant_feature_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variant_variantset_variant_feature_id_idx ON public.variant_variantset USING btree (variant_feature_id);


--
-- Name: variant_variantset_variant_feature_id_variant_type_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variant_variantset_variant_feature_id_variant_type_id_idx ON public.variant_variantset USING btree (variant_feature_id, variant_type_id);


--
-- Name: variant_variantset_variant_feature_id_variantset_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variant_variantset_variant_feature_id_variantset_id_idx ON public.variant_variantset USING btree (variant_feature_id, variantset_id);


--
-- Name: variant_variantset_variant_feature_id_variantset_id_variant_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variant_variantset_variant_feature_id_variantset_id_variant_idx ON public.variant_variantset USING btree (variant_feature_id, variantset_id, variant_type_id);


--
-- Name: variant_variantset_variantset_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variant_variantset_variantset_id_idx ON public.variant_variantset USING btree (variantset_id);


--
-- Name: variantset_name_variantset_id_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX variantset_name_variantset_id_idx ON public.variantset USING btree (name, variantset_id);


--
-- Name: analysisfeature analysisfeature_analysis_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysisfeature
    ADD CONSTRAINT analysisfeature_analysis_id_fkey FOREIGN KEY (analysis_id) REFERENCES public.analysis(analysis_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: analysisfeature analysisfeature_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analysisfeature
    ADD CONSTRAINT analysisfeature_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: chadoprop chadoprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chadoprop
    ADD CONSTRAINT chadoprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvprop cvprop_cv_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvprop
    ADD CONSTRAINT cvprop_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cv(cv_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvprop cvprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvprop
    ADD CONSTRAINT cvprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm cvterm_cv_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm
    ADD CONSTRAINT cvterm_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cv(cv_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm_dbxref cvterm_dbxref_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_dbxref
    ADD CONSTRAINT cvterm_dbxref_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm_dbxref cvterm_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_dbxref
    ADD CONSTRAINT cvterm_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm cvterm_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm
    ADD CONSTRAINT cvterm_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm_relationship cvterm_relationship_object_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship
    ADD CONSTRAINT cvterm_relationship_object_id_fkey FOREIGN KEY (object_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm_relationship cvterm_relationship_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship
    ADD CONSTRAINT cvterm_relationship_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvterm_relationship cvterm_relationship_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvterm_relationship
    ADD CONSTRAINT cvterm_relationship_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermpath cvtermpath_cv_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cv(cv_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermpath cvtermpath_object_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_object_id_fkey FOREIGN KEY (object_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermpath cvtermpath_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermpath cvtermpath_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermpath
    ADD CONSTRAINT cvtermpath_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermprop cvtermprop_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermprop
    ADD CONSTRAINT cvtermprop_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE;


--
-- Name: cvtermprop cvtermprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermprop
    ADD CONSTRAINT cvtermprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE;


--
-- Name: cvtermsynonym cvtermsynonym_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermsynonym
    ADD CONSTRAINT cvtermsynonym_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: cvtermsynonym cvtermsynonym_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cvtermsynonym
    ADD CONSTRAINT cvtermsynonym_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: dbxref dbxref_db_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxref
    ADD CONSTRAINT dbxref_db_id_fkey FOREIGN KEY (db_id) REFERENCES public.db(db_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: dbxrefprop dbxrefprop_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxrefprop
    ADD CONSTRAINT dbxrefprop_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: dbxrefprop dbxrefprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dbxrefprop
    ADD CONSTRAINT dbxrefprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvterm feature_cvterm_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm
    ADD CONSTRAINT feature_cvterm_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvterm_dbxref feature_cvterm_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_dbxref
    ADD CONSTRAINT feature_cvterm_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvterm_dbxref feature_cvterm_dbxref_feature_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_dbxref
    ADD CONSTRAINT feature_cvterm_dbxref_feature_cvterm_id_fkey FOREIGN KEY (feature_cvterm_id) REFERENCES public.feature_cvterm(feature_cvterm_id) ON DELETE CASCADE;


--
-- Name: feature_cvterm feature_cvterm_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm
    ADD CONSTRAINT feature_cvterm_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvterm_pub feature_cvterm_pub_feature_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_pub
    ADD CONSTRAINT feature_cvterm_pub_feature_cvterm_id_fkey FOREIGN KEY (feature_cvterm_id) REFERENCES public.feature_cvterm(feature_cvterm_id) ON DELETE CASCADE;


--
-- Name: feature_cvterm feature_cvterm_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm
    ADD CONSTRAINT feature_cvterm_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvterm_pub feature_cvterm_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvterm_pub
    ADD CONSTRAINT feature_cvterm_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_cvtermprop feature_cvtermprop_feature_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvtermprop
    ADD CONSTRAINT feature_cvtermprop_feature_cvterm_id_fkey FOREIGN KEY (feature_cvterm_id) REFERENCES public.feature_cvterm(feature_cvterm_id) ON DELETE CASCADE;


--
-- Name: feature_cvtermprop feature_cvtermprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_cvtermprop
    ADD CONSTRAINT feature_cvtermprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_dbxref feature_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_dbxref
    ADD CONSTRAINT feature_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_dbxref feature_dbxref_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_dbxref
    ADD CONSTRAINT feature_dbxref_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature feature_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_expression feature_expression_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expression
    ADD CONSTRAINT feature_expression_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_expression feature_expression_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expression
    ADD CONSTRAINT feature_expression_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_expressionprop feature_expressionprop_feature_expression_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expressionprop
    ADD CONSTRAINT feature_expressionprop_feature_expression_id_fkey FOREIGN KEY (feature_expression_id) REFERENCES public.feature_expression(feature_expression_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_expressionprop feature_expressionprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_expressionprop
    ADD CONSTRAINT feature_expressionprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_genotype feature_genotype_chromosome_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype
    ADD CONSTRAINT feature_genotype_chromosome_id_fkey FOREIGN KEY (chromosome_id) REFERENCES public.feature(feature_id) ON DELETE SET NULL;


--
-- Name: feature_genotype feature_genotype_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype
    ADD CONSTRAINT feature_genotype_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE;


--
-- Name: feature_genotype feature_genotype_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_genotype
    ADD CONSTRAINT feature_genotype_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE;


--
-- Name: feature feature_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_phenotype feature_phenotype_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_phenotype
    ADD CONSTRAINT feature_phenotype_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE;


--
-- Name: feature_pub feature_pub_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pub
    ADD CONSTRAINT feature_pub_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_pub feature_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pub
    ADD CONSTRAINT feature_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_pubprop feature_pubprop_feature_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pubprop
    ADD CONSTRAINT feature_pubprop_feature_pub_id_fkey FOREIGN KEY (feature_pub_id) REFERENCES public.feature_pub(feature_pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_pubprop feature_pubprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_pubprop
    ADD CONSTRAINT feature_pubprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationship feature_relationship_object_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship
    ADD CONSTRAINT feature_relationship_object_id_fkey FOREIGN KEY (object_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationship_pub feature_relationship_pub_feature_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship_pub
    ADD CONSTRAINT feature_relationship_pub_feature_relationship_id_fkey FOREIGN KEY (feature_relationship_id) REFERENCES public.feature_relationship(feature_relationship_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationship_pub feature_relationship_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship_pub
    ADD CONSTRAINT feature_relationship_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationship feature_relationship_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship
    ADD CONSTRAINT feature_relationship_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationship feature_relationship_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationship
    ADD CONSTRAINT feature_relationship_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationshipprop feature_relationshipprop_feature_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop
    ADD CONSTRAINT feature_relationshipprop_feature_relationship_id_fkey FOREIGN KEY (feature_relationship_id) REFERENCES public.feature_relationship(feature_relationship_id) ON DELETE CASCADE;


--
-- Name: feature_relationshipprop_pub feature_relationshipprop_pub_feature_relationshipprop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop_pub
    ADD CONSTRAINT feature_relationshipprop_pub_feature_relationshipprop_id_fkey FOREIGN KEY (feature_relationshipprop_id) REFERENCES public.feature_relationshipprop(feature_relationshipprop_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationshipprop_pub feature_relationshipprop_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop_pub
    ADD CONSTRAINT feature_relationshipprop_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature_relationshipprop feature_relationshipprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature_relationshipprop
    ADD CONSTRAINT feature_relationshipprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: feature feature_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureloc featureloc_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc
    ADD CONSTRAINT featureloc_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureloc_pub featureloc_pub_featureloc_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc_pub
    ADD CONSTRAINT featureloc_pub_featureloc_id_fkey FOREIGN KEY (featureloc_id) REFERENCES public.featureloc(featureloc_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureloc_pub featureloc_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc_pub
    ADD CONSTRAINT featureloc_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureloc featureloc_srcfeature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureloc
    ADD CONSTRAINT featureloc_srcfeature_id_fkey FOREIGN KEY (srcfeature_id) REFERENCES public.feature(feature_id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureprop featureprop_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop
    ADD CONSTRAINT featureprop_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureprop_pub featureprop_pub_featureprop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop_pub
    ADD CONSTRAINT featureprop_pub_featureprop_id_fkey FOREIGN KEY (featureprop_id) REFERENCES public.featureprop(featureprop_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureprop_pub featureprop_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop_pub
    ADD CONSTRAINT featureprop_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: featureprop featureprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.featureprop
    ADD CONSTRAINT featureprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: gwas_run gwas_run_subpopulation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_run
    ADD CONSTRAINT gwas_run_subpopulation_id_fkey FOREIGN KEY (subpopulation_id) REFERENCES public.gwas_subpopulation(gwas_subpopulation_id);


--
-- Name: gwas_run gwas_run_trait_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_run
    ADD CONSTRAINT gwas_run_trait_id_fkey FOREIGN KEY (trait_id) REFERENCES public.gwas_trait(gwas_trait_id);


--
-- Name: gwas_trait gwas_trait_phenotype_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gwas_trait
    ADD CONSTRAINT gwas_trait_phenotype_id_fkey FOREIGN KEY (phenotype_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: indel_featureloc indel_featureloc_indel_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_featureloc
    ADD CONSTRAINT indel_featureloc_indel_feature_id_fkey FOREIGN KEY (indel_feature_id) REFERENCES public.indel_feature(indel_feature_id);


--
-- Name: indel_featureloc indel_featureloc_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_featureloc
    ADD CONSTRAINT indel_featureloc_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id);


--
-- Name: indel_featureloc indel_featureloc_srcfeature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indel_featureloc
    ADD CONSTRAINT indel_featureloc_srcfeature_id_fkey FOREIGN KEY (srcfeature_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_dj123_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_dj123_contig_id_fkey FOREIGN KEY (dj123_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_from_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_from_contig_id_fkey FOREIGN KEY (from_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_from_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_from_organism_id_fkey FOREIGN KEY (from_organism_id) REFERENCES public.organism(organism_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_ir64_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_ir64_contig_id_fkey FOREIGN KEY (ir64_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_kasalath_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_kasalath_contig_id_fkey FOREIGN KEY (kasalath_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_nb_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_nb_contig_id_fkey FOREIGN KEY (nb_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_rice9311_contig_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_rice9311_contig_id_fkey FOREIGN KEY (rice9311_contig_id) REFERENCES public.feature(feature_id);


--
-- Name: mv_convertpos_nb2allrefs mv_convertpos_nb2allrefs_snp_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mv_convertpos_nb2allrefs
    ADD CONSTRAINT mv_convertpos_nb2allrefs_snp_feature_id_fkey FOREIGN KEY (snp_feature_id) REFERENCES public.snp_feature(snp_feature_id);


--
-- Name: organism_dbxref organism_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism_dbxref
    ADD CONSTRAINT organism_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: organism_dbxref organism_dbxref_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organism_dbxref
    ADD CONSTRAINT organism_dbxref_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: organismprop organismprop_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organismprop
    ADD CONSTRAINT organismprop_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: organismprop organismprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organismprop
    ADD CONSTRAINT organismprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: platform platform_db_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT platform_db_id_fkey FOREIGN KEY (db_id) REFERENCES public.db(db_id);


--
-- Name: platform platform_genotyping_method_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT platform_genotyping_method_id_fkey FOREIGN KEY (genotyping_method_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: platform platform_variantset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT platform_variantset_id_fkey FOREIGN KEY (variantset_id) REFERENCES public.variantset(variantset_id);


--
-- Name: pub_dbxref pub_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_dbxref
    ADD CONSTRAINT pub_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pub_dbxref pub_dbxref_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_dbxref
    ADD CONSTRAINT pub_dbxref_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pub_relationship pub_relationship_object_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship
    ADD CONSTRAINT pub_relationship_object_id_fkey FOREIGN KEY (object_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pub_relationship pub_relationship_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship
    ADD CONSTRAINT pub_relationship_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pub_relationship pub_relationship_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub_relationship
    ADD CONSTRAINT pub_relationship_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pub pub_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pub
    ADD CONSTRAINT pub_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pubauthor pubauthor_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubauthor
    ADD CONSTRAINT pubauthor_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pubprop pubprop_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubprop
    ADD CONSTRAINT pubprop_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: pubprop pubprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pubprop
    ADD CONSTRAINT pubprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: sample_varietyset sample_varietyset_db_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sample_varietyset
    ADD CONSTRAINT sample_varietyset_db_id_fkey FOREIGN KEY (db_id) REFERENCES public.db(db_id);


--
-- Name: sample_varietyset sample_varietyset_stock_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sample_varietyset
    ADD CONSTRAINT sample_varietyset_stock_sample_id_fkey FOREIGN KEY (stock_sample_id) REFERENCES public.stock_sample(stock_sample_id);


--
-- Name: snp_feature snp_feature_variantset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_feature
    ADD CONSTRAINT snp_feature_variantset_id_fkey FOREIGN KEY (variantset_id) REFERENCES public.variantset(variantset_id);


--
-- Name: snp_featureloc snp_featureloc_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureloc
    ADD CONSTRAINT snp_featureloc_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id);


--
-- Name: snp_featureloc snp_featureloc_snp_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureloc
    ADD CONSTRAINT snp_featureloc_snp_feature_id_fkey FOREIGN KEY (snp_feature_id) REFERENCES public.snp_feature(snp_feature_id);


--
-- Name: snp_featureloc snp_featureloc_srcfeature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureloc
    ADD CONSTRAINT snp_featureloc_srcfeature_id_fkey FOREIGN KEY (srcfeature_id) REFERENCES public.feature(feature_id);


--
-- Name: snp_featureprop snp_featureprop_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureprop
    ADD CONSTRAINT snp_featureprop_feature_id_fkey FOREIGN KEY (snp_feature_id) REFERENCES public.snp_feature(snp_feature_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: snp_featureprop snp_featureprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.snp_featureprop
    ADD CONSTRAINT snp_featureprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_cvterm stock_cvterm_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm
    ADD CONSTRAINT stock_cvterm_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_cvterm stock_cvterm_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm
    ADD CONSTRAINT stock_cvterm_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_cvterm stock_cvterm_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvterm
    ADD CONSTRAINT stock_cvterm_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_cvtermprop stock_cvtermprop_stock_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvtermprop
    ADD CONSTRAINT stock_cvtermprop_stock_cvterm_id_fkey FOREIGN KEY (stock_cvterm_id) REFERENCES public.stock_cvterm(stock_cvterm_id) ON DELETE CASCADE;


--
-- Name: stock_cvtermprop stock_cvtermprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_cvtermprop
    ADD CONSTRAINT stock_cvtermprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_dbxref stock_dbxref_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxref
    ADD CONSTRAINT stock_dbxref_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock stock_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_dbxref stock_dbxref_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxref
    ADD CONSTRAINT stock_dbxref_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_dbxrefprop stock_dbxrefprop_stock_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxrefprop
    ADD CONSTRAINT stock_dbxrefprop_stock_dbxref_id_fkey FOREIGN KEY (stock_dbxref_id) REFERENCES public.stock_dbxref(stock_dbxref_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_dbxrefprop stock_dbxrefprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_dbxrefprop
    ADD CONSTRAINT stock_dbxrefprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_genotype stock_genotype_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_genotype
    ADD CONSTRAINT stock_genotype_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE;


--
-- Name: stock stock_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES public.organism(organism_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_phenotype2 stock_phenotype2_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype2
    ADD CONSTRAINT stock_phenotype2_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id);


--
-- Name: stock_phenotype2 stock_phenotype2_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype2
    ADD CONSTRAINT stock_phenotype2_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: stock_phenotype stock_phenotype_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype
    ADD CONSTRAINT stock_phenotype_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id);


--
-- Name: stock_phenotype stock_phenotype_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype
    ADD CONSTRAINT stock_phenotype_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id);


--
-- Name: stock_phenotype stock_phenotype_stock_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype
    ADD CONSTRAINT stock_phenotype_stock_type_id_fkey FOREIGN KEY (stock_type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: stock_phenotype stock_phenotype_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_phenotype
    ADD CONSTRAINT stock_phenotype_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: stock_pub stock_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_pub
    ADD CONSTRAINT stock_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_pub stock_pub_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_pub
    ADD CONSTRAINT stock_pub_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship_cvterm stock_relationship_cvterm_cvterm_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_cvterm
    ADD CONSTRAINT stock_relationship_cvterm_cvterm_id_fkey FOREIGN KEY (cvterm_id) REFERENCES public.cvterm(cvterm_id) ON DELETE RESTRICT;


--
-- Name: stock_relationship_cvterm stock_relationship_cvterm_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_cvterm
    ADD CONSTRAINT stock_relationship_cvterm_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE RESTRICT;


--
-- Name: stock_relationship_cvterm stock_relationship_cvterm_stock_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_cvterm
    ADD CONSTRAINT stock_relationship_cvterm_stock_relationship_id_fkey FOREIGN KEY (stock_relationship_id) REFERENCES public.stock_relationship(stock_relationship_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship stock_relationship_object_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship
    ADD CONSTRAINT stock_relationship_object_id_fkey FOREIGN KEY (object_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship_pub stock_relationship_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_pub
    ADD CONSTRAINT stock_relationship_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship_pub stock_relationship_pub_stock_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship_pub
    ADD CONSTRAINT stock_relationship_pub_stock_relationship_id_fkey FOREIGN KEY (stock_relationship_id) REFERENCES public.stock_relationship(stock_relationship_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship stock_relationship_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship
    ADD CONSTRAINT stock_relationship_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_relationship stock_relationship_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_relationship
    ADD CONSTRAINT stock_relationship_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stock_sample stock_sample_dbxref_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_sample
    ADD CONSTRAINT stock_sample_dbxref_id_fkey FOREIGN KEY (dbxref_id) REFERENCES public.dbxref(dbxref_id);


--
-- Name: stock_sample stock_sample_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock_sample
    ADD CONSTRAINT stock_sample_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id);


--
-- Name: stock stock_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockcollection_stock stockcollection_stock_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection_stock
    ADD CONSTRAINT stockcollection_stock_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockcollection_stock stockcollection_stock_stockcollection_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection_stock
    ADD CONSTRAINT stockcollection_stock_stockcollection_id_fkey FOREIGN KEY (stockcollection_id) REFERENCES public.stockcollection(stockcollection_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockcollection stockcollection_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollection
    ADD CONSTRAINT stockcollection_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE;


--
-- Name: stockcollectionprop stockcollectionprop_stockcollection_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollectionprop
    ADD CONSTRAINT stockcollectionprop_stockcollection_id_fkey FOREIGN KEY (stockcollection_id) REFERENCES public.stockcollection(stockcollection_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockcollectionprop stockcollectionprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockcollectionprop
    ADD CONSTRAINT stockcollectionprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: stockprop_pub stockprop_pub_pub_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop_pub
    ADD CONSTRAINT stockprop_pub_pub_id_fkey FOREIGN KEY (pub_id) REFERENCES public.pub(pub_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockprop_pub stockprop_pub_stockprop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop_pub
    ADD CONSTRAINT stockprop_pub_stockprop_id_fkey FOREIGN KEY (stockprop_id) REFERENCES public.stockprop(stockprop_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockprop stockprop_stock_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop
    ADD CONSTRAINT stockprop_stock_id_fkey FOREIGN KEY (stock_id) REFERENCES public.stock(stock_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: stockprop stockprop_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stockprop
    ADD CONSTRAINT stockprop_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: synonym synonym_cvterm_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.synonym
    ADD CONSTRAINT synonym_cvterm_fk FOREIGN KEY (type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: tmp_cds_handler_relationship tmp_cds_handler_relationship_cds_row_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tmp_cds_handler_relationship
    ADD CONSTRAINT tmp_cds_handler_relationship_cds_row_id_fkey FOREIGN KEY (cds_row_id) REFERENCES public.tmp_cds_handler(cds_row_id) ON DELETE CASCADE;


--
-- Name: variantset varantset_variant_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variantset
    ADD CONSTRAINT varantset_variant_type_id_fkey FOREIGN KEY (variant_type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: variant_variantset variant_variantset_variant_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variant_variantset
    ADD CONSTRAINT variant_variantset_variant_type_id_fkey FOREIGN KEY (variant_type_id) REFERENCES public.cvterm(cvterm_id);


--
-- Name: variant_variantset variant_variantset_variantset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.variant_variantset
    ADD CONSTRAINT variant_variantset_variantset_id_fkey FOREIGN KEY (variantset_id) REFERENCES public.variantset(variantset_id);



GRANT ALL ON SCHEMA public TO postgres;


--
-- PostgreSQL database dump complete
--

