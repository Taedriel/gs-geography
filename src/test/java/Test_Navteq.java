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

package org.graphstream.geography.test;

import org.graphstream.geography.shp.GeoSourceNavteq;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

/**
 * Test the import of a road network from a Navteq shapefile;
 * 
 * @author Antoine Dutot
 * @author Merwan Achibet
 */
public class Test_Navteq {

	public static void main(String[] args) {

		// XXX Exception raised when using a SingleGraph. Weird.
		Graph graph = new MultiGraph("road network");
		graph.addAttribute("stylesheet", Test_Navteq.style);

		GeoSourceNavteq src = new GeoSourceNavteq("/home/merwan/navteq/Zlevels_lehavre.shp", "/home/merwan/navteq/Streets_lehavre.shp");
		src.addSink(graph);

		src.read();

		src.end();

		graph.display(false);
	}

	private static final String style = "node { size: 3px; text-visibility-mode: hidden; }";

}
