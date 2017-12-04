Capsule Corp
===

Lib de base pour scala, notamment pour la sérialisation JSON avec `play-json` et la configuration avec `kxbmap.configs`.

## Installation

Importer la lib dans les dépendences sbt :

    libraryDependencies += "com.kreactive" %% "capsule-corp" % "0.5.7"
        
Importer la lib pour play dans les dépendences sbt :

    libraryDependencies += "com.kreactive" %% "capsule-corp-play" % "0.5.7"
        
Pour utiliser le testkit (dans les tests) :

    libraryDependencies += "com.kreactive" %% "capsule-corp-testkit" % "0.5.7" % "test"

## Capsule
Fournit un `Ordering`, un `Format` JSON et un `Configs` qui permettent de faire des `value class` propres et transparentes.
Expose aussi un testkit fondé sur `scalatest` qui permet de tester rapidement que les différents outils sont proprement implémentés.
 
Fournit aussi un `Format[Map[K, V]]` si le type `V` a un `Format` et si le type `K` a une correspondance avec `String` (implicitement si `K` est une `StringValueClass`) 

### Capsule-Play
Fournit un trait `PlayValueClass[V, C]` qui doit étendre un `ValueClass[V, C]`, pour rajouter au type un `QueryStringBindable[C]` et un `PathBindable[C]`

## Coproduct
Fournit une façon simple d'implémenter un `OFormat[T]` quand `T` est un `sealed trait` composé  de `case class` et de `case object`. Le JSON formé possède un champ `t` qui correspond au type et un champ `d` qui correspond aux données, validées indépendemment pour chaque sous-type.

## Enum
Fournit une façon simple d'implémenter un `Format[T]` quand `T` est un `sealed trait` composé de `case object`. 
Chaque objet est sérialisé par son `toString`. Fournit aussi un ordre implicite sur les valeurs.

## Extractor
Fournit des extracteurs (à utiliser dans le _pattern matching_) typés pour les JSON et la config, à partir des déserialiseurs implicites.
Fournit aussi un extracteur JSON pour les `String`.

## Lens
Une implémentation simple des Lens, qui pourra grossir selon les besoins.



cross publish sur bintray :

    + publish          // cross publish sur les version scala

    bintrayRelease     // crée la release de la version


Crédits :

Cyrille Corpet      https://github.com/zozoens31

Julien Blondeau     https://github.com/captainju

Rémi Lavolée        https://github.com/rlavolee


