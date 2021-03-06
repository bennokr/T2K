/**
 * Copyright (C) 2015 T2K-Team, Data and Web Science Group, University of
							Mannheim (t2k@dwslab.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dwslab.T2K.matching.dbpedia.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.lucene.util.CommandLineUtil;

import au.com.bytecode.opencsv.CSVWriter;
import de.dwslab.T2K.index.io.DefaultIndex;
import de.dwslab.T2K.matching.Matcher;
import de.dwslab.T2K.matching.dbpedia.components.Preprocessing;
import de.dwslab.T2K.matching.dbpedia.components.ValueBasedComponent;
import de.dwslab.T2K.matching.dbpedia.components.WebtableToDBpediaMatchingProcess;
import de.dwslab.T2K.matching.dbpedia.logging.MatchingScoreAggregator;
import de.dwslab.T2K.matching.dbpedia.logging.MissingValueLogger;
import de.dwslab.T2K.matching.dbpedia.matchers.instance.ValueBasedInstanceMatcher;
import de.dwslab.T2K.matching.dbpedia.model.MatchingEvaluation;
import de.dwslab.T2K.matching.dbpedia.model.MatchingResult;
import de.dwslab.T2K.matching.dbpedia.model.settings.EvaluationParameters;
import de.dwslab.T2K.matching.dbpedia.model.settings.MatchingParameters;
import de.dwslab.T2K.matching.evaluation.EvaluationResult;
import de.dwslab.T2K.matching.process.Configuration;
import de.dwslab.T2K.similarity.functions.QuadraticSimilarityDecorator;
import de.dwslab.T2K.similarity.functions.date.WeightedDatePartSimilarity;
import de.dwslab.T2K.similarity.functions.numeric.DeviationSimilarity;
import de.dwslab.T2K.similarity.functions.numeric.NormalisedNumericSimilarity;
import de.dwslab.T2K.similarity.functions.string.GeneralisedStringJaccard;
import de.dwslab.T2K.similarity.functions.string.LevenshteinSimilarity;
import de.dwslab.T2K.tableprocessor.model.Table;
import de.dwslab.T2K.utils.concurrent.Consumer;
import de.dwslab.T2K.utils.concurrent.Parallel;
import de.dwslab.T2K.utils.data.ValueAggregator;
import de.dwslab.T2K.utils.io.SynchronizedTextWriter;
import de.dwslab.T2K.utils.query.Func;
import de.dwslab.T2K.utils.query.Q;
import de.dwslab.T2K.utils.timer.Timer;
import static de.dwslab.T2K.matching.dbpedia.components.ValueBasedComponent.PAR_STRING_SIMILARITY_FOR_SETS;

public class main {

    public static Long getTime(Class<?> cl) {
        try {
            String rn = cl.getName().replace('.', '/') + ".class";
            JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
            return j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

    	String version = "";
    	
        Long build = getTime(main.class);
        if (build != null) {
        	version = String.format("dbpedia.matching Version 1 Build #%s", DateFormatUtils.format(build, "yyyy-MM-dd HH:mm:ss"));
            System.out.println(version);
            System.err.println(version);
        }

        if (args.length < 13) {

            System.out
                    .println("Usage: <DBpedia directory> <DBpedia index> <use units> <instance goldstandard> <property goldstandard> <property ranges> <equivalent properties> <class goldstandard> <webtable directory> <surface form file> <redirect file> <verbose> <configuration> [uri index path] [stop]");

        } else {
            boolean end = false;

            String dbp = args[0];
            String idx = args[1];
            Boolean useUnits = Boolean.parseBoolean(args[2]);
            String inst = args[3];
            String prop = args[4];
            String propClasses = args[5];
            String equiv = args[6];
            String cls = args[7];
            String web = args[8];
            String surface = args[9];
            String redirects = args[10];
            boolean verbose = Boolean.parseBoolean(args[11]);
            String configPath = args[12];
            String uriIndexPath ="";
            String eqProps = args[13];
            if(args.length==15) {
                uriIndexPath = args[14];
            }
            //Double threshold = Double.parseDouble(args[10]);

//            boolean optimise = false;
//            if(args.length>11) {
//                optimise = Boolean.parseBoolean(args[11]);
//            }

//            String fastjoin = null;
//            if (args.length > 12) {
//                fastjoin = args[12];
//            }

            boolean stop = true;
            if (args.length > 15) {
                stop = Boolean.parseBoolean(args[15]);
            }

            Timer mainTimer = new Timer("Matching");

            //WebtableToDBpediaMatcher m = new WebtableToDBpediaMatcher();
            final WebtableToDBpediaMatchingProcess m = new WebtableToDBpediaMatchingProcess();
            Timer tdbp = new Timer("Load DBpedia", mainTimer);

            final MatchingParameters loadDbpParams = new MatchingParameters();
            loadDbpParams.setUseUnitDetection(useUnits);
            m.setMatchingParameters(loadDbpParams);

            if (new File(idx).exists()) {
                m.setLuceneIndex(new DefaultIndex(idx));
            }

            m.loadDBpedia(dbp);
            tdbp.stop();          
            
            //Preprocessing.setSurfaceFormPath(surface);
            //Preprocessing.setRedirectsPath(redirects);
            Preprocessing.loadSurfaceForms(surface);
            Preprocessing.loadRedirects(redirects);
            m.getData().loadEquivalentProperties(eqProps);
            
            
            while (!end) {

                final MatchingParameters params = new MatchingParameters();
                params.setUseUnitDetection(useUnits);
                //params.setFastJoinPath(fastjoin);
                params.setCollectMatchingInfo(verbose);
                final EvaluationParameters eval = new EvaluationParameters();

                eval.setInstanceGoldStandardLocation(inst);
                eval.setPropertyGoldStandardLocation(prop);
                eval.setPropertyRangeGoldstandardLocation(propClasses);
                eval.setEquivalentPropertiesLocation(equiv);
                eval.setPropertyRangesLocation(new File(new File(equiv).getParent(), "propertyRanges.tsv").getAbsolutePath());
                eval.setCorrectedInstancesLocation(new File(new File(equiv).getParent(), "correctedInstances.tsv").getAbsolutePath());
                eval.setClassHierarchyLocation(new File(new File(equiv).getParent(), "superclasses.tsv").getAbsolutePath());
                eval.setClassGoldStandardLocation(cls);

                //params.setInstanceScoreThreshold(threshold);
                m.setMatchingParameters(params);
                m.setEvaluationParameters(eval);
                if(uriIndexPath!=null && !uriIndexPath.isEmpty()) {
                    m.setUriIndex(new DefaultIndex(uriIndexPath));
                }
                
                Map<String, Object> aliases = new HashMap<>();
                aliases.put("QuadraticDeviation", new QuadraticSimilarityDecorator<Double>(new DeviationSimilarity()));
                aliases.put("QuadraticNormalized", new QuadraticSimilarityDecorator<Double>(new NormalisedNumericSimilarity()));
                aliases.put("GeneralizedLevenshtein", new GeneralisedStringJaccard(new LevenshteinSimilarity(), 0.5, 0.5));
                aliases.put("WeightedDate", new WeightedDatePartSimilarity(0, 0, 1));
                aliases.put("lodtable", WebtableToDBpediaMatchingProcess.tableType.lodtable);
                aliases.put("webtable", WebtableToDBpediaMatchingProcess.tableType.webtable);
                Configuration conf = null;
                try {
                    conf = Configuration.readConfiguration(configPath, m.getParameters(), aliases);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                    return;
                }
                
                Integer numThreads = (Integer)conf.getValue(WebtableToDBpediaMatchingProcess.PAR_MAX_PARALLEL);
                System.out.println("PARALLEL: " +numThreads);
                if(numThreads>0) {
                    Parallel.SetDefaultNumProcessors(numThreads);
                }

                final Collection<MatchingResult> results = new LinkedList<MatchingResult>();
                final Collection<HashMap<String, HashMap<String, EvaluationResult>>> steps = new LinkedList<HashMap<String, HashMap<String, EvaluationResult>>>();

                final HashSet<String> matchedTables = new HashSet<>();
                
                try {
                    List matched = FileUtils.readLines(new File("matched_tables"));
                    matchedTables.addAll(matched);
                } catch(Exception ex) {
                    
                }
                
                final SynchronizedTextWriter doneWriter = new SynchronizedTextWriter("matched_tables", true);
                
                Timer t = Timer.getNamed("Matching v1 build #" + getTime(main.class), null);
                m.setParentTimer(t);
                if (new File(web).isDirectory()) {
                    final int ttl = new File(web).listFiles().length;
                    int current = 1;
                    final ValueAggregator baseF1 = new ValueAggregator();
                    final ValueAggregator resultF1 = new ValueAggregator();
//                    for (File f : new File(web).listFiles()) {
//                        System.out.println("=====================================================");
//                        System.out.println("=====================================================");
//                        System.out.println(String.format("Matching table %d/%d", current++, ttl));
//                        System.out.println("=====================================================");
//                        System.out.println(String.format("Current performance %.2f (%.2f baseline)", resultF1.getAvg(), baseF1.getAvg()));
//                        System.out.println("=====================================================");
//
//                        //MatchingResult r = m.matchWebTable(f.getAbsolutePath());
//                        conf.getConfig().put(m.PAR_WEBTABLE, f.getAbsolutePath());
//                        m.run(conf);
//                        MatchingResult r = m.getResult();
//                        if (r.getEvaluation().getInstanceBaseLine() != null) {
//                            baseF1.AddValue(r.getEvaluation().getInstanceBaseLine().getF1Score());
//                            resultF1.AddValue(r.getEvaluation().getInstanceResult().getF1Score());
//                            results.add(r);
//                            steps.add(m.getIntermediateResults());
//                        }
//                    }
                    
                    Collection<File> files = Arrays.asList(new File(web).listFiles());
                    files = Q.without(files, matchedTables, new Func<String, File>() {

                        @Override
                        public String invoke(File in) {
                            return in.getName();
                        }
                    });
                    
                    
                    // running several matching processes in parallel can only work with individual instances, as intermediate results are stored as class member variables ...
                    final Configuration configFinal = conf;
                    try {
                        //new Parallel<File>().foreach(Arrays.asList(new File(web).listFiles()), new Consumer<File>() {
                        new Parallel<File>().foreach(files, new Consumer<File>() {

                            @Override
                            public void execute(File parameter) {
                                //if(!matchedTables.contains(parameter.getName())) {
                                    try {
                                        WebtableToDBpediaMatchingProcess p = new WebtableToDBpediaMatchingProcess();
                                        p.setMatchingParameters(loadDbpParams);
                                        p.setData(m.getData().shallowCopy());
                                        p.setMatchingParameters(params);
                                        p.setEvaluationParameters(eval);
                                        p.setLuceneIndex(m.getKeyIndex().getLuceneIndex());
                                        if(m.getUriIndex()!=null) {
                                            p.setUriIndex(m.getUriIndex());
                                        }
                                        
                                        Configuration c = configFinal.clone();
                                        c.getConfig().put(p.PAR_WEBTABLE, parameter.getAbsolutePath());
                                        p.run(c);
                                        MatchingResult r = p.getResult();
                                        if(r!=null && (Boolean)c.getValue(WebtableToDBpediaMatchingProcess.PAR_EVALUATE)) {
                                            if (r.getEvaluation().getInstanceBaseLine() != null) {
                                                synchronized (results) {
                                                    baseF1.AddValue(r.getEvaluation().getInstanceBaseLine().getF1Score());
                                                    if(r.getEvaluation().getInstanceResult()!=null) {
                                                        resultF1.AddValue(r.getEvaluation().getInstanceResult().getF1Score());
                                                    }
                                                    results.add(r);
                                                    steps.add(p.getIntermediateResults());
                                                }
                                            }
                                        }
                                    } catch(Exception e) {
                                        System.err.println(String.format("Matching Table %s failed!", parameter.getName()));
                                        e.printStackTrace();
                                    }
                                    
                                    doneWriter.write(parameter.getName());
//                                } else {
//                                    System.err.println(String.format("Skipping %s (already matched)", parameter.getName()));
//                                }
                            }

                        }, mainTimer, version);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    doneWriter.flushAndBlock();
                    
                } else {
                    conf.getConfig().put(m.PAR_WEBTABLE, web);
                    m.run(conf);
                    MatchingResult r = m.getResult();
                    if((Boolean)conf.getValue(WebtableToDBpediaMatchingProcess.PAR_EVALUATE)) {
                        results.add(m.matchWebTable(web));
                    }
                    results.add(r);
                    steps.add(m.getIntermediateResults());
                }
                t.stop();

                if (!stop) {
                    System.err.println(t.toString());
                }

                if((Boolean)conf.getValue(WebtableToDBpediaMatchingProcess.PAR_EVALUATE)) {
                    MatchingScoreAggregator.printResults(results, steps);
                    MissingValueLogger.writeMissingValues(results);
                }
                

                System.err.flush();

                if (!stop) {
                    boolean userInputOk = false;
                    do {

                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println();

                        System.out
                                .println("run [again] or run [new] task or [quit]?");

                        BufferedReader cin = new BufferedReader(
                                new InputStreamReader(System.in));

                        String cmd = "";
                        try {
                            cmd = cin.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (cmd.equalsIgnoreCase("quit")) {
                            end = true;
                            userInputOk = true;
                        } else if (cmd.equalsIgnoreCase("new")) {
                            System.out
                                    .println("Enter: <DBpedia directory> <DBpedia index> <use units> <instance goldstandard> <property goldstandard> <equivalent properties> <class goldstandard> <webtable directory> <verbose> [fastjoin path]");

                            ProcessBuilder pb = new ProcessBuilder("dummy", System
                                    .console().readLine());

                            List<String> values = pb.command();

                            if (values.size() > 8) {
                                dbp = values.get(1);
                                idx = values.get(2);
                                useUnits = Boolean.parseBoolean(values.get(3));
                                inst = values.get(4);
                                prop = values.get(5);
                                equiv = values.get(6);
                                cls = values.get(7);
                                web = values.get(8);
                                verbose = Boolean.parseBoolean(values.get(9));

                            } else {
                                userInputOk = false;
                            }
                        } else if (cmd.equalsIgnoreCase("again")) {
                            userInputOk = true;
                        }

                    } while (!userInputOk);
                } else {
                    end = true;
                }
            }

            mainTimer.stop();
            System.err.println(mainTimer.toString());
        }
    }
}
