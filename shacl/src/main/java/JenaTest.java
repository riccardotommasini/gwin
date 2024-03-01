import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.jena.graph.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.jena.sparql.graph.GraphFactory;
import org.streamreasoning.rsp4j.api.RDFUtils;

import java.util.Collection;

public class JenaTest {

    public static Node transformNode(org.apache.commons.rdf.api.RDFTerm term){
        if(term instanceof IRI){
            return NodeFactory.createURI(((IRI) term).getIRIString());
        }else if(term instanceof BlankNode){
            return NodeFactory.createBlankNode(((BlankNode) term).uniqueReference());
        }else{
            return NodeFactory.createLiteral(((Literal) term).getLexicalForm());
        }
    }
    public static void main(String[] args){
        System.out.println("Hello");


        String SHAPES = "/Users/weiqinxu/Documents/work_playground/jena_test/src/main/java/shapes.ttl";
        String DATA = "/Users/weiqinxu/Documents/work_playground/jena_test/src/main/java/data1.ttl";

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        String PREFIX = "http://rsp4j.io/covid/";
        RDF instance = RDFUtils.getInstance();
        org.apache.commons.rdf.api.Graph graph = instance.createGraph();
        IRI a = instance.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        IRI person = instance.createIRI("http://rsp4j.io/covid#Person");
        IRI room = instance.createIRI("http://rsp4j.io/covid#Room");
        String eventID = "1";
        String randomPerson = "Bob";
        String randomRoom = "redRoom";

        graph.add(instance.createTriple(instance.createIRI(PREFIX + "_observation" + eventID), a, instance.createIRI(PREFIX + "RFIDObservation")));
        graph.add(instance.createTriple(instance.createIRI(PREFIX + "_observation" + eventID), instance.createIRI(PREFIX + "where"), instance.createIRI(PREFIX + randomRoom)));
        graph.add(instance.createTriple(instance.createIRI(PREFIX + "_observation" + eventID), instance.createIRI(PREFIX + "who"), instance.createIRI(PREFIX + randomPerson)));
        graph.add(instance.createTriple(instance.createIRI(PREFIX + randomPerson), instance.createIRI(PREFIX + "isIn"), instance.createIRI(PREFIX + randomRoom)));
        graph.add(instance.createTriple(instance.createIRI(PREFIX + randomPerson), a, person));
        graph.add(instance.createTriple(instance.createIRI(PREFIX + randomRoom), a, room));


        Graph j_g = GraphFactory.createGraphMem();
        for(org.apache.commons.rdf.api.Triple triple: graph.iterate()){
            j_g.add(new Triple(transformNode(triple.getSubject()), transformNode(triple.getPredicate()), transformNode(triple.getObject())));
        }

        //org.apache.commons.rdf.jena.JenaRDF jena_rdf = new org.apache.commons.rdf.jena.JenaRDF();
        //Graph j_graph = jena_rdf.asJenaGraph(graph);

        Shapes shapes = Shapes.parse(shapesGraph);

        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
        report.getEntries().forEach(re -> System.out.println(re.message()));
        System.out.println("-----------------------------------------------------------------");
        ShLib.printReport(report);
        System.out.println("-----------------------------------------------------------------");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}

