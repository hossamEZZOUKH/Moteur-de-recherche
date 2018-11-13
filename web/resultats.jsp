
<%@page import="java.io.File"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="m2i.mr.model.FileRanking"%>
<%@page import="m2i.mr.model.Resultat"%>
<%@page import="m2i.mr.indexation.Index"%>
<%@page contentType="text/html"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta http-equiv="Content-Type">
        <title>Mini Projet | MR</title>

        <!-- css Bootstrap -->
        <link href="./assets/bootstrap.min.css" rel="stylesheet">
        <link href="./assets/bootstrap-theme.min.css" rel="stylesheet"> 

        <!-- demo page styles -->
        <link href="./assets/jplist.demo-pages.min.css" rel="stylesheet" type="text/css">
        <link href="./assets/bootstrap-checkbox.css" rel="stylesheet" type="text/css">
        <link href="./assets/style-nlp-team.css" rel="stylesheet" type="text/css">
        <link href="./assets/style.css" rel="stylesheet" type="text/css">	
    </head>
    <body>
        <!-- top bar -->
        <div id="barSite">
        </div>
        <div id="logoSite" class="box">
            <div class="center">			
                <div class="box">
                    <div class="nlp-header">
                        <div class="logo-fso">
                            <a href="#" target="_blank"><img src="./images/faculte.png" alt="fso"></a>
                        </div>
                        <div class="logo-nlp">
                            <a href="#" target="_blank"><img src="./images/toto.png" alt="fso"></a>

                        </div>	
                        <div class="logo-ump">
                            <a href="#" target="_blank"><img src="./images/univercity.png" alt="ump"></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>	


        <!-- Head  -->
        <header id="header" class="box">
            <div id="header-box" class="box">
                <div class="center">			
                    <div class="box">
                        <!-- logo -->
                        <div class="align-center text-shadow" id="logo">	
                            <p><a>Master M2I</a></p>

                        </div>
                    </div>
                </div>					
            </div>
        </header>

        <!-- Head  -->
        <div class="container">
            <div class="text-center">
                <div id="centerContainer">
                    <div class="tab-content" id="myTabContent">
                        <div class="modal-dialog">
                            <div class="loginmodal-container">
                                <h1>saisir votre requête</h1><br>
                                <form method="post" action="Home">
                                    <table>
                                        <tr>
                                            <td><input type="text" name="iApogee" placeholder="Requête ... "></td>
                                            <td><input type="submit" name="connexion" class="login loginmodal-submit" value="RECHERCHE"></td>
                                        </tr>
                                    </table>

                                    <div style="text-align: left;margin-left: 6px;">
                                        </b>Rechercher par : </b>
                                        <input type="radio" name="type_recherche" value="word" checked> Mot
                                        <input type="radio" name="type_recherche" value="stem"> Stem
                                        <input type="radio" name="type_recherche" value="lemme"> Lemme 
                                    </div>


                                    <div style="text-align: left;margin-left: 6px;">
                                        </b>Langue de requête : </b>
                                        <input type="radio" name="traduction_source" value="ar" checked> Arabe
                                        <input type="radio" name="traduction_source" value="fr"> Français
                                        <input type="radio" name="traduction_source" value="en"> anglais 
                                    </div>

                                    <div style="text-align: left;margin-left: 6px;">
                                        </b>Traduire vers : </b>
                                        <input type="radio" name="traduction_d" value="ar" checked> Arabe
                                        <input type="radio" name="traduction_d" value="fr"> Français
                                        <input type="radio" name="traduction_d" value="en"> anglais 
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>	 		
                </div>

                <br><br><br>

                <%
                    Index.setRootPathCorpus(this.getServletContext().getRealPath(File.separator + "MultedCorpus"));

                    String words = request.getParameter("iApogee");
                    String l1 = request.getParameter("traduction_source");
                    String l2 = request.getParameter("traduction_d");
                    String mode = request.getParameter("type_recherche");

                    Index.index();
                    Resultat search = Index.search(words, l1, l2, mode);
                    
                    %>
                <div id="Team" class="tab-pane fade in">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h3 class="panel-title" style="color:#FFFFFF;text-align: left;">
                                RESULTATS : <% out.print(search.getSearchWords()); %>
                            </h3>
                        </div>
                    </div>
                </div>
                <%
                    TreeMap<String, TreeMap<String, List<FileRanking>>> resultatByWords = search.getResultatByFiles();
                    List<String> files = search.getFiles();
                    //System.err.println(resultatByWords);

                    for (Map.Entry<String, TreeMap<String, List<FileRanking>>> entry : resultatByWords.entrySet()) {
                        String key0 = entry.getKey();
                        TreeMap<String, List<FileRanking>> value = entry.getValue();
                        for (Map.Entry<String, List<FileRanking>> en : value.entrySet()) {
                            String key = en.getKey();
                            System.err.println("File : " + key);
                            List<FileRanking> val = en.getValue();
                %>
                <div class="panel-body yt-card" style="border-color: #e4e4e4;margin-bottom: -6px;">
                    <table class='gridtable' border='0'>
                        <tr>
                            <td style='padding-right: 5px;'><%
                                Map<String, String> top = Index.map.get(Index.index.get(key0)).get("0");
                                out.print("<b>Auteur : " + top.get("Speaker") + "<br/>");
                                out.print("Titre : " + top.get("Title") + "<br/></b>");
                                for (FileRanking elem : val) {
                                    for (String id : elem.getIds()) {
                                        out.print("<div style='color: #86be57'> Segment " + id + " : " + Index.map.get(elem.getId()).get(id).get(l2) + "</div>");
                                    }
                                }
                                %></td>
                            <td style='padding-right: 5px;text-align: left;'  width='200px'><a href="MultedCorpus/Corpus/<% out.print(key0); %>"><div class="p-3 mb-2 bg-primary text-white" style="width: 100px;height:50px;text-align: center;padding: 13px;">détail >></div></a></td>
                        </tr>
                    </table>
                </div>
                <%
                        }
                    }
                %>

            </div> 
        </div>



        <br><br><br>

        <div class="divFooter">
            <center>
                <span style="color:white;">
                    <a style="color:white;" target="_blank" href="#">2017 </a> © 2018 <a style="color:white;" target="_blank" href="#">Bourhim Moaad & Tounsi Souhail</a>
                </span>
            </center>
        </div>
    </body>
</html>