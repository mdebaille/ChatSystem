# ChatSystem

Version Java requise: 1.<version de l'INSA>

Comment compiler et exécuter le projet:
[...]


Fonctionnalités implémentées:

	- Connexion 
	- Deconnexion
	- Choix d'un pseudo
	- Discussion avec un utilisateur connecté:
		- Sélection d'un utilisateur dans la liste
		- Ouverture d'une fenêtre de chat
		- Envoi de messages à un utilisateur
		- Envoi de fichiers à un utilisateur
	- Envoi à un groupe d'utilisateur:
		- Sélection d'un groupe d'utilisateur dans la liste
		- Ouverture d'une fenêtre de chat
		- Envoi de messages à un group d'utilisateurs
		- Envoi de fichiers à un groupe d'utilisateurs
	- Réception de messages
	- Réception de fichiers
	- Enregistrement de l'historique des messages envoyés/reçus pour chaque utilisateur
	- Notification indiquant la réception d'un message/fichier

	
Rapport de tests:

Test UsersModel

- testReceivedMessageUser()

receivedMessageUser() est appelée par l'instance de MulticastListener dès qu'elle reçoit un MessageUser provenant du réseau.
Selon le type de message reçu (connexion ou déconnexion), la méthode receivedMessageUser() doit ajouter ou supprimer 
l'utilisateur associé de la liste des utilisateurs.

Initialement, la liste des utilisateurs est vide

	- Réception d'un message de connexion d'un utilisateur 1
		Résultat: la liste contient 1 utilisateur et l'utilisateur ajouté est l'émetteur du MessageUser => OK
	- Réception d'un message de connexion d'un utilisateur 2
		Résultat: la liste contient 2 utilisateurs et l'utilisateur ajouté est l'émetteur du MessageUser => OK
	- Réception d'un 2ème message de connexion de l'utilisateur 2
		Résultat: la liste contient 2 utilisateurs => OK
	- Réception d'un message de déconnexion de l'utilisateur 2
		Résultat: la liste contient 1 utilisateur, l'utilsateur 2 n'est plus dans la liste => OK
	- Réception d'un nouveau message de déconnexion de l'utilisateur 2
		Résultat: rien ne se passe => OK 
	- Réception d'un message de connexion de l'utilisateur 1, mais le pseudo est erroné
		Résultat: la liste contient 1 utilisateur, l'utilisateur 1 est enregistré sous le pseudo du premier message => OK
	- Réception d'un message de déconnexion de l'utilisateur 1, mais le pseudo est erroné
		Résultat: la liste est vide, l'utilisateur 1 a été supprimé => OK
	- Réception de notre propre message de connexion
		Résultat: rien ne se passe => OK

- testUpdateList()

Cette méthode teste le thread permettant de supprimer des utilisateurs de la liste si on ne reçoit pas de MessageUser de connexion
de leur part en l'espace de 6s.
	
Initialement, la liste des utilisateurs est vide

	- Attente de 6s après réception d'un message de connexion d'un utilisateur 1, la liste contient donc un utilisateur
		Résultat: la liste contient 1 utilisateur => OK
	- Attente de 6s après réception de 3 messages de connexion d'un utilisateur 2
		Résultat: la liste contient 1 utilisateur, l'utilisateur 1 a été supprimé => OK
	- Attente de 6s
		Résultat: la liste est vide, l'utilisateur 2 a été supprimé => OK

Test MessagesModel

- testAddMessage()

addMessage() sauvegarde un message dans l'historique des messages envoyés/reçus d'un chat, cette méthode du modèle doit notifier les potentiels observateurs de MessagesModel (ChatIHM) d'un nouveau message afin qu'ils puissent l'afficher. De plus, à chaque création d'un observateur, celui-ci doit récupérer tous les messages de la liste et les afficher.
	
Initialement, la liste de messages est vide, aucun objet n'observe le modèle (pas de fenêtre de chat ouverte)

	- Ajout de 3 messages
		Résultat: la liste contient 3 messages => OK
	- Ajout d'un observateur: ouverture d'une fenêtre de chat
		Résultat: la liste contient 3 messages et la méthode updateMessage() de l'observateur est appelée 3 fois => OK
	- Ajout de 2 messages
		Résultat: la liste contient 5 messages et la méthode updateMessage() de l'observateur est appelée 2 fois => OK
	- Ajout d'un observateur: ouverture d'une nouvelle fenêtre de chat
		Résultat: la méthode updateMessage() de l'observateur est appelée 5 fois => OK
	- Ajout de 2 messages
		Résultat: la méthode updateMessage() est appelée 2 fois pour chaque observateur => OK

Les messages sont bien récupérés et affichés dans le bon ordre par les observateurs, le premier message arrivée est le premier à
être affiché".
	
- testAdd1000Messages()

Test pour vérifier que le comportement du modèle ne change pas selon sa charge en messages. Ce test reprend exactement les mêmes cas que testAddMessage() mais 1000 messages sont ajoutés au modèle au lieu de 2 ou 3. Les résultats attendus sont corrects, l'ajout de 1000 messages entraîne l'affichage de 1000 messages par les observateurs et la liste se remplit en conséquence.
	
Test MulticastListener

- testMulticastListener()

MulticastListener est le thread qui écoute le réseau et gère la réception des MessageUser, il notifie UsersModel avec les messages reçus. On vérifie ici que les messages sont bien reçus et que leurs champs sont bien désérialisés par le thread.
	
Initialement, la liste des utilisateurs est vide et le thread écoute le port multicast

	- Envoi d'un paquet de connexion: Pseudo 1
		Résultat: la liste contient 1 utilisateur => OK
	- Envoi d'un paquet de connexion: Pseudo 2
		Résultat: la liste contient 2 utilisateurs => OK
	- Envoi d'un paquet de déconnexion: Pseudo 1
		Résultat: la liste contient 1 utilisateur, le pseudo 1 a été supprimé => OK
	- Envoi d'un paquet de déconnexion: Pseudo 2
		Résultat: la liste est vide, l'utilisateur a été supprimé => OK
	- Envoi de 1000 paquets de connexions différentes
		Résultat: la liste contient 1000 utilisateurs => OK
	
		
	
