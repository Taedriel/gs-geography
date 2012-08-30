/*
 * Copyright 2006 - 2012 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

package org.graphstream.geography.osm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.graphstream.geography.AttributeFilter;
import org.graphstream.geography.ElementDescriptor;
import org.graphstream.geography.ElementShape;
import org.graphstream.geography.ElementDiff;
import org.graphstream.geography.FileDescriptor;
import org.graphstream.geography.Polygon;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This geographical source implementation produces a neighborhood graph
 * according to euclidean distance.
 * 
 * The user needs to specify the distance threshold under which two buildings
 * are considered neighbors.
 * 
 * @author Merwan Achibet
 */
public class GeoSourceOSM_Neighborhood extends GeoSourceOSM {

	/**
	 * The neighborhood radius. If two buildings are separated by a distance
	 * less than this threshold, then they are considered neighbors.
	 */
	protected double radius;

	/**
	 * The descriptor matching geographic objects with representations of
	 * buildings.
	 */
	protected ElementDescriptor buildingDescriptor;

	/**
	 * The attribute filter for the buildings.
	 */
	protected AttributeFilter buildingAttributeFilter;

	/**
	 * Instantiate a new OpenStreetMap source producing a neighborhood graph.
	 * 
	 * @param fileName
	 *            The path to the input file.
	 * @param radius
	 *            The radius threshold under which two buildings are considered
	 *            neighbors.
	 */
	public GeoSourceOSM_Neighborhood(double radius, String... fileNames) {
		super(fileNames);

		this.radius = radius;

		// By default, there is no attribute worth keeping.

		this.buildingAttributeFilter = new AttributeFilter();

		// We are only interested in buildings.

		this.buildingDescriptor = new ElementDescriptor(this, "BUILDINGS", this.buildingAttributeFilter);

		this.buildingDescriptor.mustBe(ElementShape.Type.POLYGON);
		this.buildingDescriptor.mustHave("building", "yes");

		// Attach this descriptor to every file.

		for(String fileName : fileNames) {

			FileDescriptor fileDescriptor = new FileDescriptor(fileName);
			fileDescriptor.addDescriptor(this.buildingDescriptor);

			this.addFileDescriptor(fileDescriptor);
		}
	}

	/**
	 * Give the descriptor matching geographic objects with representations of
	 * buildings.
	 * 
	 * @return The building descriptor.
	 */
	public ElementDescriptor getBuildingDescriptor() {

		return this.buildingDescriptor;
	}

	/**
	 * Give the attribute filter for the buildings.
	 * 
	 * @return the attribute filter.
	 */
	public AttributeFilter getBuildingAttributeFilter() {

		return this.buildingAttributeFilter;
	}

	@Override
	public void nextEvents() {

		// Keep a record of the buildings that have already been inserted into
		// the graph.

		HashMap<String, Coordinate> placedBuildings = new HashMap<String, Coordinate>();

		ArrayList<ElementDiff> elementDiffsAtStep = getElementDiffsAtStep(this.currentTimeStep); // TODO

		for(ElementDiff elementDiff : elementDiffsAtStep) {

			// If the diff is a base, insert the building.

			if(elementDiff.isBase()) {

				// Compute the center of the current building and add a new node
				// at this position.

				String id = elementDiff.getElementId();

				sendNodeAdded(this.id, id);

				Coordinate centroid = ((Polygon)elementDiff.getShape()).getCentroid();
				sendNodeAttributeAdded(this.id, id, "x", centroid.x);
				sendNodeAttributeAdded(this.id, id, "y", centroid.y);

				// Draw an edge between the new node and already placed ones if
				// their distance is below the neighborhood radius.

				for(Entry<String, Coordinate> idPosPair : placedBuildings.entrySet())
					if(centroid.distance(idPosPair.getValue()) < this.radius)
						sendEdgeAdded(this.id, id + idPosPair.getKey(), id, idPosPair.getKey(), false);

				// Record the this building has been added to the graph.

				placedBuildings.put(id, centroid);
			}
		}
		
		// Update attributes and shape.

		// TODO
	}

}
