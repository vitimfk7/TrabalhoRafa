package Controle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Contato;
import modelo.dao.ContatoDAO;
import modelo.dao.DAOFactory;

public class Financeiro extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String caminho = request.getServletPath();        
        if (caminho.equalsIgnoreCase("/Financeiro/Orcamento")){
            try {
                DAOFactory fabrica = new DAOFactory();
                fabrica.abrirConexao();
                ContatoDAO dao = fabrica.criarContatoDAO();
                RequestDispatcher rd = request.getRequestDispatcher("/mostrarcontatosid.jsp");
                long id = Long.parseLong(request.getParameter("id"));
                Contato contatos = dao.buscar(id);
                fabrica.fecharConexao();
                double fundos = contatos.getFundos();
                double liquido = Liquido(fundos);
                boolean posNeg = posNeg(fundos);
                String balanco;
                if (posNeg == true){
                    balanco = "Balanço positivo";
                }else{
                    balanco = "Balanço negativo";
                }
                request.setAttribute("contatos", contatos);
                request.setAttribute("liquido", liquido);
                request.setAttribute("taxas", Taxas(fundos));
                request.setAttribute("contas", Contas(fundos));
                request.setAttribute("balanco", balanco);
                rd.forward(request, response);
            } catch (SQLException ex) {
                DAOFactory.mostrarSQLException(ex);
            }
        }
    }
    
    public boolean posNeg (double fundos){
        double valor = Contas(fundos);
        boolean balanco;
        if (valor > 0){
            balanco = true;
        }else{ 
            balanco = false;
        }
        return balanco;
    }

    public double Liquido(double fundos) {
        double lqd = fundos - Taxas(fundos);
        return lqd;
        
    }
    
    public double Taxas(double fundos) {
        double taxas = fundos * 15/100 ;
        return taxas;   
    }
    
    public double Contas (double fundos){
        double contas = 240;
        double abate = Liquido(fundos) - contas;
        return abate;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Financeiro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Financeiro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    

    

}
