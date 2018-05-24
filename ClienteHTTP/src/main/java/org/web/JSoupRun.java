package org.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;

public class JSoupRun {
    private static String url = "";
    private static Document doc;
    private static String PROTOCOLO = "http://";

    public static void main(String[] args) throws IOException{
        CapturarUrl();
    }

    private static int ContarLineasHTML(Document doc){
        int cantidadLineas = doc.html().split("\r?\n").length;

        return cantidadLineas;
    }

    private static int ContarParrafosHTML(Document doc) {
        int cantidadParrafos = doc.getElementsByTag("p").size();

        return cantidadParrafos;
    }

    private static int ContarImagenesEnParrafosHTML(Document doc) {
        int cantidadImagenesParrafos = doc.select("p img").size();

        return cantidadImagenesParrafos;
    }

    private static int ContarFormulariosGETHTML(Document doc) {
        int cantidadFormulariosGET = doc.select("form[method=get],form[method=GET]").size();

        return cantidadFormulariosGET;
    }


    private static int ContarFormulariosPOSTHTML(Document doc) {
        int cantidadFormulariosPOST = doc.select("form[method=post],form[method=POST]").size();

        return cantidadFormulariosPOST;
    }

    private static Elements GetInputsHTML(Document doc) {
        Elements inputs = doc.select("form input[type]");

        return inputs;
    }

    private static void MostrarInputsHTML(Document doc) {
        Elements inputs = GetInputsHTML(doc);
        if (inputs.size() <= 0)
            return;
        System.out.println("\nInputs y sus tipos");
        for (Element input: inputs) {
            System.out.println("\nCampo: " + input.toString() + "\nTipo: " + input.attr("type"));
        }
    }

    private static Elements GetFormulariosPostHTML(Document doc){
        Elements formulariosPost = doc.select("form[method=post],form[method=POST]");
        return formulariosPost;
    }

    private static void EnviarPeticionesFormulariosPOST(Document doc)
    {
        Document docRequest = null;
        try {
            if (GetFormulariosPostHTML(doc).size() <= 0){
                System.out.println("\nNo hay formularios POST");
                return;
            }

            int contador = 1;
            for (Element element: GetFormulariosPostHTML(doc)) {
                String rutaAbs = element.absUrl("action");

                docRequest = Jsoup.connect(rutaAbs)
                        .method(Connection.Method.POST)
                        .data("asignatura","practica1")
                        .header("matricula","20141329").post();

                System.out.println("\nRespuesta Formulario "+ contador +":\n\n" + docRequest.html());
                contador++;
            }

        }catch (IOException e){
            System.out.println("\nError al intentar mandar la petición al servidor de " + url);
        }
    }

    private static void CapturarUrl(){
        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Digite la url que quiere analizar: ");
            url = PROTOCOLO + sc.next();
            doc = Jsoup.connect(url).get();

            MostrarInformacionURL(doc);

        }
        catch(IOException e) {
            System.out.println("\nError al intentar conectarse al servidor de la url " + e.getMessage());
        }

    }

    private static void MostrarInformacionURL(Document doc)
    {
        System.out.println("\nNúmero de líneas: " + ContarLineasHTML(doc));
        System.out.println("Número de párrafos: " + ContarParrafosHTML(doc));
        System.out.println("Número de imágenes en párrafos: " + ContarImagenesEnParrafosHTML(doc) );
        System.out.println("Numero de formularios con método GET: " + ContarFormulariosGETHTML(doc));
        System.out.println("Numero de formularios con método POST: " + ContarFormulariosPOSTHTML(doc));
        MostrarInputsHTML(doc);
        EnviarPeticionesFormulariosPOST(doc);
    }
}
