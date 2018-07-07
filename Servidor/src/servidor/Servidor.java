/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author marialead
 */
public class Servidor {

    /**
     * @param args the command line arguments
     * 
     */
     static final int port = 5000;
     public static ObjectInputStream in;
     public static DataOutputStream ou;
    private static Connection conn;
    public static void main(String[] args) {
        
    String database = "jdbc:postgresql://localhost:5432/proyecto";  
        
    System.setProperty("javax.net.ssl.keyStore", "Keytool/serverKey.jks");
    System.setProperty("javax.net.ssl.keyStorePassword","servpass");
    System.setProperty("javax.net.ssl.trustStore", "Keytool/serverTrustedCerts.jks");
    System.setProperty("javax.net.ssl.trustStorePassword", "servpass");
    
   
        SSLServerSocketFactory sslServerSocketFactory = 
                (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
         
        try {
            ServerSocket sslServerSocket =sslServerSocketFactory.createServerSocket(port);

            Socket socket = sslServerSocket.accept();             
  
            try{
                in = new ObjectInputStream(socket.getInputStream());
                ou = new DataOutputStream(socket.getOutputStream());
                while (true){
                    int opcion=in.readInt();
                    if(opcion==1)
                        Servidor.registrarUsuario();
                    if(opcion==2)
                        Servidor.iniciarSesion();
                    if(opcion==3)
                        Servidor.generarCertificado();
                        
                }
            }
            catch(Exception e) {
                
            }
            
            System.out.println("Closed");
             
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    public static void registrarUsuario() throws ClassNotFoundException, SQLException, IOException{
                            System.out.println("entro en registar");
                    List<String> lista =(List<String>)in.readObject();
                    System.out.println(lista.toString());
                    String urlDatabase =  "jdbc:postgresql://localhost:5432/proyecto";
                    Class.forName("org.postgresql.Driver");
                                conn = DriverManager.getConnection(urlDatabase,"postgres", "24277447");
                                conn.setAutoCommit(true);

                                Statement statement = conn.createStatement();
                                
                                String insert_user = "INSERT INTO usuario (nombre,clave,correo)"
                                            + "VALUES ('" + lista.get(0) + "','" + lista.get(1) + "','" + lista.get(2)  + "');";
                                statement.executeUpdate(insert_user);
                    System.out.println("cliente aceptado");
    }

    private static void iniciarSesion() throws IOException, ClassNotFoundException, SQLException {
                    System.out.println("entro en iniciar sesion");
                    List<String> lista =(List<String>)in.readObject();
                    System.out.println(lista.toString());
                     String urlDatabase =  "jdbc:postgresql://localhost:5432/proyecto";
                    Class.forName("org.postgresql.Driver");
                                conn = DriverManager.getConnection(urlDatabase,"postgres", "24277447");
                                conn.setAutoCommit(true);

                                Statement statement = conn.createStatement();
                                String select_user = "SELECT * FROM usuario where correo = '"+lista.get(0)+"'";
                                
                                ResultSet result = statement.executeQuery(select_user);
                                
                                if(!result.isBeforeFirst() && result.getRow() == 0){
                                    ou.writeInt(0);
                                    Statement statementupdate = conn.createStatement();
                                    String update_user="UPDATE USUARIO SET veces = veces + 1 where correo = '"+lista.get(0)+"'";
                                    statementupdate.executeUpdate(update_user);
                                    return;
                                }
                                
                                else{
                                    result.next();
                                    int cant=result.getInt("veces");
                                    Boolean bloqueado = result.getBoolean("bloqueado");
                                    if(cant >= 3 || bloqueado){
                                        ou.writeInt(2);
                                        Statement statementupdate2 = conn.createStatement();
                                        String update_user="UPDATE USUARIO SET bloqueado = true where correo = '"+lista.get(0)+"'";
                                        statementupdate2.executeUpdate(update_user);
                                        return;
                                    }
                                    String clave = result.getString("clave");
                                    if(clave.equals(lista.get(1))){
                                        ou.writeInt(1);
                                        Statement statementupdate = conn.createStatement();
                                        String update_user="UPDATE USUARIO SET veces = 0 where correo = '"+lista.get(0)+"'";
                                        statementupdate.executeUpdate(update_user);
                                        return;
                                    }
                                }
                                ou.writeInt(0);
                                Statement statementupdate = conn.createStatement();
                                String update_user="UPDATE USUARIO SET veces = veces + 1 where correo = '"+lista.get(0)+"'";
                                statementupdate.executeUpdate(update_user);
                                return;
                                
                                
    }

    private static void generarCertificado() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
