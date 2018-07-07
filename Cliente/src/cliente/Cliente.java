/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
 

/**
 *
 * @author marialead
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    
    static final int port = 5000;
    public static ObjectOutputStream ou;
    public static DataInputStream oi;
    public static void main(String[] args) {
        
    System.setProperty("javax.net.ssl.keyStore", "Keytool/clientKey.jks");
    System.setProperty("javax.net.ssl.keyStorePassword","clientpass");
    System.setProperty("javax.net.ssl.trustStore", "Keytool/clientTrustedCerts.jks");
    System.setProperty("javax.net.ssl.trustStorePassword", "clientpass");
        
        SSLSocketFactory sslSocketFactory = 
                (SSLSocketFactory)SSLSocketFactory.getDefault();
        try {
            Socket socket = sslSocketFactory.createSocket("localhost", port);
            //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ou = new ObjectOutputStream(socket.getOutputStream());
            oi = new DataInputStream(socket.getInputStream());
                System.out.println("Conectado al servidor");
                Principal principal = new Principal();
                principal.setVisible(true); 
            
             
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    public static void enviarRegistro (String nombre,String clave, String correo){
        try {
            ou.writeInt(1);
            List<String> lista = new ArrayList<String>();
            lista.add(nombre);
            lista.add(clave);
            lista.add(correo);
            System.out.print("Entro para enviar registro del ususario");
            ou.writeUnshared(lista);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public static int iniciarSesion (String correo,String clave){
        try {
            ou.writeInt(2);
            List<String> lista = new ArrayList<String>();
            lista.add(correo);
            lista.add(clave);
            System.out.print("Entro para enviar inicio de sesion");
            ou.writeUnshared(lista);
            int opcion = oi.readInt();
            return opcion;
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    
    }
}
