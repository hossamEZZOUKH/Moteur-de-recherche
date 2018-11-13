<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
            <div>
                <div class="container">
                    <div class="row">
                    </div>
                </div>
            </div> 
            <br>
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
                                        <input type="radio" name="type_recherche" value="all"> Lemme 
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
            </div> 
        </div>
        <br>

        <div class="divFooter">
            <center>
                <span style="color:white;">
                    <a style="color:white;" target="_blank" href="#">2017 </a>© 2018 <a style="color:white;" target="_blank" href="#">Bourhim Moaad & Tounsi Souhail</a>
                </span>
            </center>
        </div>
    </body>
</html>