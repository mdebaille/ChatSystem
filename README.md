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

TestUsersModel: 
	- testReceivedMessageUser()
	receivedMessageUser() appelée par l'instance de MulticastListener dès qu'elle reçoit un MessageUser provenant du réseau.
	Selon le type de message reçu (connexion ou déconnexion), la méthode receivedMessageUser() doit ajouter ou supprimer 
	l'utilisateur associé de la liste des utilisateurs.
	
	Initialement, la liste des utilisateurs est vide
	Cas de tests:
		- Réception d'un message de connexion d'un utilisateur 1
		Résultat: la liste contient 1 utilisateur et l'utilisateur ajouté est l'émetteur du MessageUser => OK
		- Réception d'un message de connexion d'un utilisateur 2
		Résultat: la liste contient 2 utilisateurs et l'utilisateur ajouté est l'émetteur du MessageUser => OK
		- Réception d'un 2ème message de connexion de l'utilisateur 2
		Résultat: la liste contient 2 utilisateurs => OK
		- Réception d'un message de déconnexion de l'utilisateur 2
		Résultat: 
		[...]
	- testUpdateList()
	[...]
	
	je continue plus tard
		
		
	
	
	
		
	
