# TPContacts V3 — Fragments, RecyclerView, filtrage et Google Maps

Application Android de gestion de contacts (TP Android V3) écrite en Kotlin avec
des interfaces XML. Une seule `MainActivity` héberge trois fragments dynamiques.

## Prérequis

- Android Studio (JDK 17+) et Android SDK 37
- Une clé API Google Maps (facultative pour compiler, nécessaire pour afficher la carte)

## Configuration de la clé Google Maps

La clé n'est **jamais** versionnée. Elle est lue depuis `local.properties`
(fichier ignoré par Git) puis injectée dans le manifest via un placeholder :

```properties
# local.properties
MAPS_API_KEY=AIza...votre_cle...
```

Sans clé, le reste de l'application fonctionne ; la carte reste grise et affiche
un message d'aide.

## Architecture

```
com.example.tpcontacts
├── MainActivity              // activité unique, FragmentContainerView
├── data/ContactRepository    // collection en mémoire (singleton applicatif)
├── model/Contact             // id, nom, prénom, téléphone, e-mail, photo, lat/lon
├── ui/ContactsViewModel      // ViewModel partagé (activityViewModels)
├── ui/list/
│   ├── ListeContactsFragment // recherche + RecyclerView
│   └── ContactAdapter        // ListAdapter + DiffUtil, identifiants stables
├── ui/detail/
│   └── DetailContactFragment // consultation, édition, actions, GPS
├── ui/map/
│   └── CarteContactFragment  // SupportMapFragment + marqueur
└── util/
    ├── ContactFilter         // filtrage pur et testable
    └── CoordinateValidator    // validation lat/lon
```

### Navigation

- `MainActivity.openDetail(id)` et `openMap(id)` utilisent
  `FragmentManager` + `replace()` + `addToBackStack()`.
- Le contact est transmis par **identifiant** dans un `Bundle`, jamais par position.
- Au changement de configuration, `MainActivity` ne recrée pas la liste si
  `savedInstanceState != null` : le `FragmentManager` restaure la pile existante.

### Communication entre fragments

Choix : **ViewModel partagé** (`activityViewModels<ContactsViewModel>()`).
La liste observe `LiveData<List<Contact>>` et se met à jour immédiatement après
un enregistrement dans le détail. Seul l'identifiant transite dans un `Bundle`.

### Filtrage

Le filtrage est effectué en mémoire à chaque frappe, sans bouton de validation :

- insensible à la casse (`lowercase(Locale.ROOT)`) ;
- porte sur nom, prénom et téléphone (espaces ignorés dans le numéro) ;
- texte vide → tous les contacts ; aucun résultat → message dédié ;
- la liste source n'est jamais réordonnée, et le clic utilise l'identifiant du
  contact filtré (pas d'erreur d'indice).

### Google Maps

- SDK `play-services-maps`, `SupportMapFragment` ajouté dynamiquement ;
- caméra centrée sur le contact (zoom 14) et marqueur avec nom + téléphone ;
- coordonnées absentes/invalides → message explicite, carte masquée ;
- le bouton Retour (et le bouton système) revient au détail via `popBackStack()`.

## Tests unitaires

```bash
gradlew.bat testDebugUnitTest
```

Couverture :

- `ContactFilterTest` : nom/prénom/téléphone, casse, espaces, requête vide, sans
  résultat, conservation de l'identifiant ;
- `CoordinateValidatorTest` : virgule décimale, bornes, valeurs non numériques,
  coordonnée manquante ;
- `ContactTest` : coordonnées valides/invalides.

## Fonctionnalités conservées de la V2

- Fiche détaillée et modification avec validation des champs obligatoires ;
- bouton **Appeler** (`ACTION_DIAL`) et **E-mail** (`ACTION_SENDTO`) avec gestion
  des applications indisponibles ;
- choix d'une photo dans la galerie et prise de photo avec la caméra (permission
  demandée à l'exécution, photo stockée dans le cache via `FileProvider`) ;
- **Annuler** sans enregistrer.
