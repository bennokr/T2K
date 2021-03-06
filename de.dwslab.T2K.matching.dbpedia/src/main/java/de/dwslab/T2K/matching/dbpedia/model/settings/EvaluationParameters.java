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
package de.dwslab.T2K.matching.dbpedia.model.settings;

/**
 * Parameters of the evaluation process
 * @author Oliver
 *
 */
public class EvaluationParameters {

    private String propertyGoldStandardLocation;
    /**
     * gets the directory of the property gold standard
     * @return
     */
    public String getPropertyGoldStandardLocation() {
        return propertyGoldStandardLocation;
    }
    /**
     * Sets the directory of the property gold standard
     * @param propertyGoldStandardLocation
     */
    public void setPropertyGoldStandardLocation(
            String propertyGoldStandardLocation) {
        this.propertyGoldStandardLocation = propertyGoldStandardLocation;
    }

    private String equivalentPropertiesLocation;
    /**
     * gets the path of the equivalent properties file
     * @return
     */
    public String getEquivalentPropertiesLocation() {
        return equivalentPropertiesLocation;
    }
    /**
     * sets the path of the equivalent properties file
     * @param equivalentPropertiesLocation
     */
    public void setEquivalentPropertiesLocation(
            String equivalentPropertiesLocation) {
        this.equivalentPropertiesLocation = equivalentPropertiesLocation;
    }
    
    private String propertyRangeGoldstandardLocation;
    

    private String instanceGoldStandardLocation;
    /**
     * gets the directory of the instance gold standard
     * @return
     */
    public String getInstanceGoldStandardLocation() {
        return instanceGoldStandardLocation;
    }
    /**
     * sets the directory of the instance gold standard
     * @param instanceGoldStandardLocation
     */
    public void setInstanceGoldStandardLocation(
            String instanceGoldStandardLocation) {
        this.instanceGoldStandardLocation = instanceGoldStandardLocation;
    }
    
    private String correctedInstancesLocation;
    /**
     * gets the path of the corrected instances file
     * @return
     */
    public String getCorrectedInstancesLocation() {
        return correctedInstancesLocation;
    }
    /**
     * sets the path of the corrected instances file
     * @param correctedInstancesLocation
     */
    public void setCorrectedInstancesLocation(String correctedInstancesLocation) {
        this.correctedInstancesLocation = correctedInstancesLocation;
    }

    private String classGoldStandardLocation;
    /**
     * gets the path of the class gold standard file
     * @return
     */
    public String getClassGoldStandardLocation() {
        return classGoldStandardLocation;
    }
    /**
     * sets the path of the class gold standard file
     * @param classGoldStandardLocation
     */
    public void setClassGoldStandardLocation(String classGoldStandardLocation) {
        this.classGoldStandardLocation = classGoldStandardLocation;
    }

    private String classHierarchyLocation;
    /**
     * sets the path of the class hierarchy file
     * @param classHierarchyLocation
     */
    public void setClassHierarchyLocation(String classHierarchyLocation) {
        this.classHierarchyLocation = classHierarchyLocation;
    }
    /**
     * gets the path of the class hierarchy file
     * @return
     */
    public String getClassHierarchyLocation() {
        return classHierarchyLocation;
    }

    /**
     * @return the propertyRangeGoldstandardLocation
     */
    public String getPropertyRangeGoldstandardLocation() {
        return propertyRangeGoldstandardLocation;
    }

    /**
     * @param propertyRangeGoldstandardLocation the propertyRangeGoldstandardLocation to set
     */
    public void setPropertyRangeGoldstandardLocation(String propertyRangeGoldstandardLocation) {
        this.propertyRangeGoldstandardLocation = propertyRangeGoldstandardLocation;
    }
    
    private String propertyRangesLocation;

    /**
     * @return the propertyRangesLocation
     */
    public String getPropertyRangesLocation() {
        return propertyRangesLocation;
    }

    /**
     * @param propertyRangesLocation the propertyRangesLocation to set
     */
    public void setPropertyRangesLocation(String propertyRangesLocation) {
        this.propertyRangesLocation = propertyRangesLocation;
    }
    
}
