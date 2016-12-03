       options/extend_source
       program tp3
       implicit none

! Ce programme va demander la saisie de la commande, puis on va separer les differentes
!parties de la chaine en plusieurs variables, ensuite selon l'action demandee on appelera le
!sous programme correspondant.

       character*60 COMMANDE
       integer*4 IOS,
       1         COMPTEUR,
       1         SORTIE,
       1         ERRONE,
       1         CONF,
       1         POSITION_ESPACE,
       1         DEBUT_MOT,
       1         FIN_MOT,
       1         NB_MOTS,
       1         NB_MOTS_MAX,
       1         FIN_CHAINE,
       1         TROUVER_FIN,
       1         NUM_CARACTERE,
       1         ACTION,
       1         PREMIERE_LETTRE,
       1         DERNIERE_LETTRE,
       1         INTERVALLE_MAJ_MIN,
       1         APRES_MAJ,
       1         TAILLE_COLONNE,
       1         TAILLE_LIGNE,
       1         LIGNES_DESC

       parameter(NB_MOTS_MAX = 9) !une saisie correcte ne contient pas plus de 8 mots, si
!elle en contient 9, alors la saisie sera jugee incorrecte.
       parameter(ERRONE = 1)
       parameter(SORTIE = - 1)
       parameter(ACTION = 1)  !il s'agit du 1er mot de la chaine de caracteres
       parameter(PREMIERE_LETTRE = 1)  !correspond a la 1ere lettre d'un mot
       parameter(DERNIERE_LETTRE = 18)  !correspond a la derniere lettre d'un mot
       parameter(INTERVALLE_MAJ_MIN = 32)  !nombre separant un meme caractere
!minuscule de son majuscule
       parameter(APRES_MAJ = 96)  !correspond au dernier caractere avant les MIN
       parameter(TAILLE_COLONNE = 7)
       parameter(TAILLE_LIGNE = 12)
       parameter(LIGNES_DESC = 11)

        character*19 N(TAILLE_COLONNE,TAILLE_LIGNE)
       character*19 MOTS_COMMANDE(NB_MOTS_MAX)
       character*60 DESC(LIGNES_DESC)

       write(*,*) ' '
       write(*,*) '      -----------------------------------------------------'
       write(*,*) '      | Bonjour, et bienvenue dans le programme DASHBOARD |'
       write(*,*) '      -----------------------------------------------------'
       write(*,*) ' '
       write(*,*) ' '
       write(*,*) ' Voici un rappel des fonctions disponibles pour ce DASHBOARD : '
       write(*,*) ' '
       write(*,*) '   _ TASK pour creer une tache (ex : TASK IDTACHE CIBLE AUTEUR)'
       write(*,*) ' '
       write(*,*) '   _ SHOW pour voir la description (ex : SHOW IDTACHE)'
       write(*,*) ' '
       write(*,*) '   _ REMOVE pour enlever une tache (ex : REMOVE IDTACHE)'
       write(*,*) ' '
       write(*,*) '   _ CLEAR pour effacer le DASHBOARD (ex : CLEAR)'
       write(*,*) ' '
       write(*,*) '   _ CANCEL, DONE, TODO pour modifier lHEREetat de la tache (ex : DONE IDTACHE)'
       write(*,*) ' '

! La boucle de sortie pour quitter si l'on appuie sur F10
       do while (IOS .ne. SORTIE)

! Initialisons les variables, afin de ne pas garder les anciennes valeurs pour chaque variable.
               POSITION_ESPACE = 0
               DEBUT_MOT = 0
               FIN_MOT = 0
               NB_MOTS = 0
               FIN_CHAINE = 0

! Initialisons aussi le tableau des MOTS_COMMANDE
               do COMPTEUR = ACTION, NB_MOTS_MAX
                       MOTS_COMMANDE (COMPTEUR) = ' '
               end do

! Appelons le sous prgramme qui gere la saisie de la commande et aussi la sortie, si 
!l'utilisateur le demande
               call SAISIE(COMMANDE, IOS)

               if (IOS .eq. 0) then

! Trouvons la fin de la chaine
                       FIN_CHAINE = TROUVER_FIN (COMMANDE)
                       COMPTEUR = 1
                       do while (POSITION_ESPACE .lt. FIN_CHAINE .and. NB_MOTS .lt. NB_MOTS_MAX)
                               DEBUT_MOT = POSITION_ESPACE + 1

! Decoupons les mots
                               POSITION_ESPACE = POSITION_ESPACE + index (COMMANDE (DEBUT_MOT:), ' ')
                               FIN_MOT = POSITION_ESPACE - 1

! Ensuite on les enregistre dans MOTS_COMMANDE
                               MOTS_COMMANDE (COMPTEUR) = COMMANDE (DEBUT_MOT : FIN_MOT)

! Comptons les mots
                               if (MOTS_COMMANDE (COMPTEUR) .ne. ' ') then
                                       NB_MOTS = NB_MOTS + 1
                                       COMPTEUR = COMPTEUR + 1
                               end if
                       end do

! Le programme ne doit pas tenir compte de la casse, ainsi peu importe la maniere
!dont est ecrit le mot, il sera mis en majuscule
                       do COMPTEUR = 1, NB_MOTS
                               do NUM_CARACTERE = PREMIERE_LETTRE, DERNIERE_LETTRE
                                       if (ichar(MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE))
       1 .gt. APRES_MAJ) then
                                               MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE) =
       1 char(ichar(MOTS_COMMANDE (COMPTEUR)(NUM_CARACTERE:NUM_CARACTERE)) - INTERVALLE_MAJ_MIN)
                                       end if
                               end do
                       end do

!! Affichons les mots (provisoire)
!!                     do COMPTEUR = 1, NB_MOTS
!!                             write(*,*) COMPTEUR, ': ', MOTS_COMMANDE (COMPTEUR)
!!                     end do
!!
!! Testons si le mot est bien en majuscule (etape provisoire)
!!                     write(*,*) MOTS_COMMANDE (ACTION), ': voila lHEREaction'


! Si la commande contient plus de 8 mots, on demande de recommencer

                       if (NB_MOTS .eq. NB_MOTS_MAX) then
                               write(*,*) ' '
                               write(*,*) 'ERR> Trop de mot, veuillez ressaisir'
                       else

! Maintenant, en fonction du premier mot entre, on va appeler le sous programme correspondant
                               if (MOTS_COMMANDE (ACTION) .eq. 'TASK') then
                                       call TACHE(MOTS_COMMANDE, DESC, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'SHOW') then
!                                      write(*,*) 'on appelle le sous prgrm SHOW'
                                       call SHOW(MOTS_COMMANDE, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'REMOVE') then
!                                      write(*,*) 'on appelle le sous prgrm REMOVE'
                                       call REMOVE(MOTS_COMMANDE, DESC, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'CLEAR') then
!                                      write(*,*) 'on appelle le sous prgrm CLEAR'
                                       call CLEAR(MOTS_COMMANDE, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'CANCEL') then
!                                      write(*,*) 'on appelle le sous prgrm CANCEL'
                                       call CANCEL(MOTS_COMMANDE, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'DONE') then
!                                      write(*,*) 'on appelle le sous prgrm DONE'
                                       call DONE(MOTS_COMMANDE, N)
                               else if (MOTS_COMMANDE (ACTION) .eq. 'TODO') then
!                                      write(*,*) 'on appelle le sous prgrm TODO'
                                       call TODO(MOTS_COMMANDE, N)
                               else
                                       write(*,*) ' '
                                       write(*,*) 'L''action suivante n''a pas ete',
       1 ' comprise: ', MOTS_COMMANDE (ACTION)
                               end if
                       end if
               end if
       end do
       end

