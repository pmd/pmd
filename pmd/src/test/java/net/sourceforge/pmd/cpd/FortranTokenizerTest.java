/**
 *
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.FortranTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

import org.junit.Before;
import org.junit.Test;


/**
 * @author rpelisse
 *
 */
public class FortranTokenizerTest extends AbstractTokenizerTest {

	@Before
	@Override
	public void buildTokenizer() {
		this.tokenizer = new FortranTokenizer();
		this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), "sample.for"));
	}

	@Override
	public String getSampleCode() {
		 return "       options/extend_source" + PMD.EOL +
		 "       program tp3" + PMD.EOL +
		 "       implicit none" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Ce programme va demander la saisie de la commande, puis on va separer les differentes" + PMD.EOL +
		 "!parties de la chaine en plusieurs variables, ensuite selon l'action demandee on appelera le" + PMD.EOL +
		 "!sous programme correspondant." + PMD.EOL +
		 "" + PMD.EOL +
		 "       character*60 COMMANDE" + PMD.EOL +
		 "       integer*4 IOS," + PMD.EOL +
		 "       1         COMPTEUR," + PMD.EOL +
		 "       1         SORTIE," + PMD.EOL +
		 "       1         ERRONE," + PMD.EOL +
		 "       1         CONF," + PMD.EOL +
		 "       1         POSITION_ESPACE," + PMD.EOL +
		 "       1         DEBUT_MOT," + PMD.EOL +
		 "       1         FIN_MOT," + PMD.EOL +
		 "       1         NB_MOTS," + PMD.EOL +
		 "       1         NB_MOTS_MAX," + PMD.EOL +
		 "       1         FIN_CHAINE," + PMD.EOL +
		 "       1         TROUVER_FIN," + PMD.EOL +
		 "       1         NUM_CARACTERE," + PMD.EOL +
		 "       1         ACTION," + PMD.EOL +
		 "       1         PREMIERE_LETTRE," + PMD.EOL +
		 "       1         DERNIERE_LETTRE," + PMD.EOL +
		 "       1         INTERVALLE_MAJ_MIN," + PMD.EOL +
		 "       1         APRES_MAJ," + PMD.EOL +
		 "       1         TAILLE_COLONNE," + PMD.EOL +
		 "       1         TAILLE_LIGNE," + PMD.EOL +
		 "       1         LIGNES_DESC" + PMD.EOL +
		 "" + PMD.EOL +
		 "       parameter(NB_MOTS_MAX = 9) !une saisie correcte ne contient pas plus de 8 mots, si" + PMD.EOL +
		 "!elle en contient 9, alors la saisie sera jugee incorrecte." + PMD.EOL +
		 "       parameter(ERRONE = 1)" + PMD.EOL +
		 "       parameter(SORTIE = - 1)" + PMD.EOL +
		 "       parameter(ACTION = 1)  !il s'agit du 1er mot de la chaine de caracteres" + PMD.EOL +
		 "       parameter(PREMIERE_LETTRE = 1)  !correspond a la 1ere lettre d'un mot" + PMD.EOL +
		 "       parameter(DERNIERE_LETTRE = 18)  !correspond a la derniere lettre d'un mot" + PMD.EOL +
		 "       parameter(INTERVALLE_MAJ_MIN = 32)  !nombre separant un meme caractere" + PMD.EOL +
		 "!minuscule de son majuscule" + PMD.EOL +
		 "       parameter(APRES_MAJ = 96)  !correspond au dernier caractere avant les MIN" + PMD.EOL +
		 "       parameter(TAILLE_COLONNE = 7)" + PMD.EOL +
		 "       parameter(TAILLE_LIGNE = 12)" + PMD.EOL +
		 "       parameter(LIGNES_DESC = 11)" + PMD.EOL +
		 "" + PMD.EOL +
		 "        character*19 N(TAILLE_COLONNE,TAILLE_LIGNE)" + PMD.EOL +
		 "       character*19 MOTS_COMMANDE(NB_MOTS_MAX)" + PMD.EOL +
		 "       character*60 DESC(LIGNES_DESC)" + PMD.EOL +
		 "" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '      -----------------------------------------------------'" + PMD.EOL +
		 "       write(*,*) '      | Bonjour, et bienvenue dans le programme DASHBOARD |'" + PMD.EOL +
		 "       write(*,*) '      -----------------------------------------------------'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) ' Voici un rappel des fonctions disponibles pour ce DASHBOARD : '" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '   _ TASK pour creer une tache (ex : TASK IDTACHE CIBLE AUTEUR)'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '   _ SHOW pour voir la description (ex : SHOW IDTACHE)'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '   _ REMOVE pour enlever une tache (ex : REMOVE IDTACHE)'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '   _ CLEAR pour effacer le DASHBOARD (ex : CLEAR)'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "       write(*,*) '   _ CANCEL, DONE, TODO pour modifier lHEREetat de la tache (ex : DONE IDTACHE)'" + PMD.EOL +
		 "       write(*,*) ' '" + PMD.EOL +
		 "" + PMD.EOL +
		 "! La boucle de sortie pour quitter si l'on appuie sur F10" + PMD.EOL +
		 "       do while (IOS .ne. SORTIE)" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Initialisons les variables, afin de ne pas garder les anciennes valeurs pour chaque variable." + PMD.EOL +
		 "               POSITION_ESPACE = 0" + PMD.EOL +
		 "               DEBUT_MOT = 0" + PMD.EOL +
		 "               FIN_MOT = 0" + PMD.EOL +
		 "               NB_MOTS = 0" + PMD.EOL +
		 "               FIN_CHAINE = 0" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Initialisons aussi le tableau des MOTS_COMMANDE" + PMD.EOL +
		 "               do COMPTEUR = ACTION, NB_MOTS_MAX" + PMD.EOL +
		 "                       MOTS_COMMANDE (COMPTEUR) = ' '" + PMD.EOL +
		 "               end do" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Appelons le sous prgramme qui gere la saisie de la commande et aussi la sortie, si " + PMD.EOL +
		 "!l'utilisateur le demande" + PMD.EOL +
		 "               call SAISIE(COMMANDE, IOS)" + PMD.EOL +
		 "" + PMD.EOL +
		 "               if (IOS .eq. 0) then" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Trouvons la fin de la chaine" + PMD.EOL +
		 "                       FIN_CHAINE = TROUVER_FIN (COMMANDE)" + PMD.EOL +
		 "                       COMPTEUR = 1" + PMD.EOL +
		 "                       do while (POSITION_ESPACE .lt. FIN_CHAINE .and. NB_MOTS .lt. NB_MOTS_MAX)" + PMD.EOL +
		 "                               DEBUT_MOT = POSITION_ESPACE + 1" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Decoupons les mots" + PMD.EOL +
		 "                               POSITION_ESPACE = POSITION_ESPACE + index (COMMANDE (DEBUT_MOT:), ' ')" + PMD.EOL +
		 "                               FIN_MOT = POSITION_ESPACE - 1" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Ensuite on les enregistre dans MOTS_COMMANDE" + PMD.EOL +
		 "                               MOTS_COMMANDE (COMPTEUR) = COMMANDE (DEBUT_MOT : FIN_MOT)" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Comptons les mots" + PMD.EOL +
		 "                               if (MOTS_COMMANDE (COMPTEUR) .ne. ' ') then" + PMD.EOL +
		 "                                       NB_MOTS = NB_MOTS + 1" + PMD.EOL +
		 "                                       COMPTEUR = COMPTEUR + 1" + PMD.EOL +
		 "                               end if" + PMD.EOL +
		 "                       end do" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Le programme ne doit pas tenir compte de la casse, ainsi peu importe la maniere" + PMD.EOL +
		 "!dont est ecrit le mot, il sera mis en majuscule" + PMD.EOL +
		 "                       do COMPTEUR = 1, NB_MOTS" + PMD.EOL +
		 "                               do NUM_CARACTERE = PREMIERE_LETTRE, DERNIERE_LETTRE" + PMD.EOL +
		 "                                       if (ichar(MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE))" + PMD.EOL +
		 "       1 .gt. APRES_MAJ) then" + PMD.EOL +
		 "                                               MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE) =" + PMD.EOL +
		 "       1 char(ichar(MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE)) - INTERVALLE_MAJ_MIN)" + PMD.EOL +
		 "                                       end if" + PMD.EOL +
		 "                               end do" + PMD.EOL +
		 "                       end do" + PMD.EOL +
		 "" + PMD.EOL +
		 "!! Affichons les mots (provisoire)" + PMD.EOL +
		 "!!                     do COMPTEUR = 1, NB_MOTS" + PMD.EOL +
		 "!!                             write(*,*) COMPTEUR, ': ', MOTS_COMMANDE (COMPTEUR)" + PMD.EOL +
		 "!!                     end do" + PMD.EOL +
		 "!!" + PMD.EOL +
		 "!! Testons si le mot est bien en majuscule (etape provisoire)" + PMD.EOL +
		 "!!                     write(*,*) MOTS_COMMANDE (ACTION), ': voila lHEREaction'" + PMD.EOL +
		 "" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Si la commande contient plus de 8 mots, on demande de recommencer" + PMD.EOL +
		 "" + PMD.EOL +
		 "                       if (NB_MOTS .eq. NB_MOTS_MAX) then" + PMD.EOL +
		 "                               write(*,*) ' '" + PMD.EOL +
		 "                               write(*,*) 'ERR> Trop de mot, veuillez ressaisir'" + PMD.EOL +
		 "                       else" + PMD.EOL +
		 "" + PMD.EOL +
		 "! Maintenant, en fonction du premier mot entre, on va appeler le sous programme correspondant" + PMD.EOL +
		 "                               if (MOTS_COMMANDE (ACTION) .eq. 'TASK') then" + PMD.EOL +
		 "                                       call TACHE(MOTS_COMMANDE, DESC, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'SHOW') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm SHOW'" + PMD.EOL +
		 "                                       call SHOW(MOTS_COMMANDE, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'REMOVE') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm REMOVE'" + PMD.EOL +
		 "                                       call REMOVE(MOTS_COMMANDE, DESC, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'CLEAR') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm CLEAR'" + PMD.EOL +
		 "                                       call CLEAR(MOTS_COMMANDE, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'CANCEL') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm CANCEL'" + PMD.EOL +
		 "                                       call CANCEL(MOTS_COMMANDE, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'DONE') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm DONE'" + PMD.EOL +
		 "                                       call DONE(MOTS_COMMANDE, N)" + PMD.EOL +
		 "                               else if (MOTS_COMMANDE (ACTION) .eq. 'TODO') then" + PMD.EOL +
		 "!                                      write(*,*) 'on appelle le sous prgrm TODO'" + PMD.EOL +
		 "                                       call TODO(MOTS_COMMANDE, N)" + PMD.EOL +
		 "                               else" + PMD.EOL +
		 "                                       write(*,*) ' '" + PMD.EOL +
		 "                                       write(*,*) 'L''action suivante n''a pas ete'," + PMD.EOL +
		 "       1 ' comprise: ', MOTS_COMMANDE (ACTION)" + PMD.EOL +
		 "                               end if" + PMD.EOL +
		 "                       end if" + PMD.EOL +
		 "               end if" + PMD.EOL +
		 "       end do" + PMD.EOL +
		 "       end" + PMD.EOL;
	 }

	@Test
	public void tokenizeTest() throws IOException {
		this.expectedTokenCount = 434;
		super.tokenizeTest();
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FortranTokenizerTest.class);
    }
}
