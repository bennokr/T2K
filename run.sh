dbp=dbpedia_as_tables # DBpedia directory
idx=empty # DBpedia index
useUnits=0 # use units
inst=T2DGoldStandard/entities_instance # instance goldstandard
prop=T2DGoldStandard/attributes_complete # property goldstandard
propClasses=blank # property ranges
equiv=blank # equivalent properties
cls=classes_instance.csv # class goldstandard
web=empty # webtable directory
surface=blank # surface form file
redirects=blank # redirect file
verbose=1 # verbose
configPath=blank # configuration
eqProps=blank # eqProps

java -Xmx4096m \
    -jar de.dwslab.T2K.matching.dbpedia/target/matching.dbpedia-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    $dbp  \
    $idx  \
    $useUnits  \
    $inst  \
    $prop  \
    $propClasses  \
    $equiv  \
    $cls  \
    $web  \
    $surface  \
    $redirects  \
    $verbose  \
    $configPath  \
    $eqProps \