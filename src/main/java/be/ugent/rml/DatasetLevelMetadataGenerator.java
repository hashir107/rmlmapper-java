package be.ugent.rml;

import be.ugent.rml.cli.Main;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.SimpleQuadStore;
import be.ugent.rml.store.TriplesQuads;
import be.ugent.rml.term.*;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

/**
 * Unique class -- reusable outside of the mapper
 */
public class DatasetLevelMetadataGenerator {

    public static QuadStore createMetadata(QuadStore result, String outputFile, List<Term> logicalSources, String startTimestamp,
                                String stopTimestamp, String mappingFile) {

        // Create the metadata and add to QuadStore
        Term rdfDataset = new NamedNode(String.format("file:%s", outputFile));
        Term rdfDatasetGeneration = new BlankNode("#RDFdataset_Generation");
        Term rmlProcessor = new BlankNode("#RMLProcessor");

        // <#RDF_Dataset>
        result.addTriple(rdfDataset, new NamedNode(NAMESPACES.RDF + "type"),
                new NamedNode(NAMESPACES.PROV + "Entity"));
        result.addTriple(rdfDataset, new NamedNode(NAMESPACES.RDF + "type"),
                new NamedNode(NAMESPACES.VOID + "Dataset"));
        result.addTriple(rdfDataset, new NamedNode(NAMESPACES.PROV + "generatedAtTime"),
                new Literal(Instant.now().toString(), new AbstractTerm(NAMESPACES.XSD + "dateTime")));
        result.addTriple(rdfDataset, new NamedNode(NAMESPACES.PROV + "wasGeneratedBy"),
                rdfDatasetGeneration);
        result.addTriple(rdfDataset, new NamedNode(NAMESPACES.PROV + "wasAssociatedWith"),
                rmlProcessor);

        // <#RMLProcessor>
        result.addTriple(rmlProcessor, new NamedNode(NAMESPACES.RDF + "type"),
                new NamedNode(NAMESPACES.PROV + "Agent"));
        result.addTriple(rmlProcessor, new NamedNode(NAMESPACES.PROV + "type"),
                new NamedNode(NAMESPACES.PROV + "SoftwareAgent"));


        // <#RDFdataset_Generation>
        result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.RDF + "type"),
                new NamedNode(NAMESPACES.PROV + "Activity"));
        result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.PROV + "generated"),
                rdfDataset);
        result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.PROV + "startedAtTime"),
                new Literal(startTimestamp, new AbstractTerm(NAMESPACES.XSD + "dateTime")));
        result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.PROV + "endedAtTime"),
                new Literal(stopTimestamp, new AbstractTerm(NAMESPACES.XSD + "dateTime")));
        result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.PROV + "used"),
                new NamedNode(String.format("file:%s", mappingFile)));

        for (Term logicalSource: logicalSources) {
            result.addTriple(rdfDataset, new NamedNode(NAMESPACES.PROV + "wasDerivedFrom"),
                    logicalSource);
            result.addTriple(rdfDatasetGeneration, new NamedNode(NAMESPACES.PROV + "used"),
                    logicalSource);
        }


        TriplesQuads tq = Utils.getTriplesAndQuads(result.toSimpleSortedQuadStore().getQuads(null, null, null, null));

        return result;
    }
}
