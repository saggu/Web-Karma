/*******************************************************************************
 * Copyright 2012 University of Southern California
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/

package edu.isi.karma.service;

import java.io.File;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;

public class ServiceRepository {
	
	public final String SERVICE_REPOSITORY_DIR = "service_repository/services/";
	public final String TRIPLE_DATASET_DIR = "service_repository/dataset/";
	
	private Model model;

	private static ServiceRepository _InternalInstance = null;
	public static ServiceRepository Instance()
	{
		if (_InternalInstance == null)
		{
			_InternalInstance = new ServiceRepository();

			File serviceRepository = new File(_InternalInstance.SERVICE_REPOSITORY_DIR);
			if (!serviceRepository.exists())
				serviceRepository.mkdir();
			
			_InternalInstance.createRepository();
		}
		return _InternalInstance;
	}
	
	public void createRepository() {
		Location location = new Location(this.TRIPLE_DATASET_DIR);
		Dataset dataset = TDBFactory.createDataset(location);
		this.model = dataset.getDefaultModel();
	}
	
	public Model getModel() {
		return this.model;
	}
	
	public static void main(String[] args) {
		ServiceRepository.Instance().getModel();
	}
}