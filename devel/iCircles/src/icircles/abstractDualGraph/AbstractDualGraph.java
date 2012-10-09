package icircles.abstractDualGraph;

/*
 * @author Jean Flower <jeanflower@rocketmail.com>
 * Copyright (c) 2012
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the iCircles Project.
 */

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import icircles.abstractDescription.AbstractBasicRegion;
import icircles.abstractDescription.AbstractCurve;

/**
 * A dual graph of an Euler diagram contains a vertex in each of the minimal
 * regions of that diagram and an edge between each adjacent vertex.
 */
public class AbstractDualGraph {

    static Logger logger = Logger.getLogger(AbstractDualGraph.class.getName());

    ArrayList<AbstractDualNode> nodes;
    ArrayList<AbstractDualEdge> edges;

    public AbstractDualGraph(ArrayList<AbstractBasicRegion> abrs) {
        nodes = new ArrayList<AbstractDualNode>();
        edges = new ArrayList<AbstractDualEdge>();
        // Each abr becomes a node.
        // Neighbouring abrs get edges added between them.
        for (AbstractBasicRegion abr : abrs) {
            nodes.add(new AbstractDualNode(abr));
        }
        for (AbstractDualNode n : nodes) {
            boolean found_node_again = false;
            for (AbstractDualNode n2 : nodes) {
                if (!found_node_again) {
                    if (n2 == n) {
                        found_node_again = true;
                    }
                } else {
                    AbstractCurve straddlingCurve =
                            n.abr.getStraddledContour(n2.abr);
                    if (straddlingCurve != null) {
                        add_edge(n, n2, straddlingCurve);
                    }
                }
            }
        }
    }

    private void add_edge(AbstractDualNode n, AbstractDualNode n2,
            AbstractCurve straddlingCurve) {
        AbstractDualEdge e = new AbstractDualEdge(n, n2, straddlingCurve);
        n.incidentEdges.add(e);
        n2.incidentEdges.add(e);
        edges.add(e);
    }

    public void remove(AbstractDualEdge e) {
        e.from.removeEdge(e);
        e.to.removeEdge(e);
        edges.remove(e);
    }

    public void remove(AbstractDualNode n) {
        while (n.incidentEdges.size() != 0) {
            remove(n.incidentEdges.get(0));
        }
        nodes.remove(n);
    }

    public AbstractDualEdge getLowDegreeEdge() {
        // find a lowest-degree vertex, and from that,
        // choose the edge to its lowest-degree neighbour
        logger.trace("graph is " + this.debug());

        int lowestDegree = Integer.MAX_VALUE;
        AbstractDualNode lowestDegreeNode = null;
        for (AbstractDualNode n : nodes) {
            int thisDegree = n.degree();

            if (thisDegree == 0) {
                continue; // ignore isolated nodes when picking a low-degree edge
            }
            if (thisDegree < lowestDegree) {
                lowestDegreeNode = n;
                lowestDegree = thisDegree;
            }
        }
        if (lowestDegreeNode == null) {
            return null;
        }

        lowestDegree = Integer.MAX_VALUE;
        AbstractDualEdge result = null;
        for (AbstractDualEdge e : lowestDegreeNode.incidentEdges) {
            AbstractDualNode otherNode;
            if (e.from == lowestDegreeNode) {
                otherNode = e.to;
            } else {
                try {
                    assert(e.to == lowestDegreeNode);
                } catch (AssertionError ae) {
                    logger.fatal("inconcistent graph nodes:" + ae.toString());
                }
                otherNode = e.from;
            }
            int otherDegree = otherNode.degree();
            if (otherDegree < lowestDegree) {
                lowestDegree = otherDegree;
                result = e;
            }
        }
        return result;
    }

    public int getNumEdges() {
        return edges.size();
    }

    public Iterator<AbstractDualNode> getNodeIterator() {
        return nodes.iterator();
    }

    public String debug() {
        String result = "nodes : ";
        boolean isFirst = true;
        for (AbstractDualNode n : nodes) {
            if (!isFirst) {
                result += ",";
            } else {
                isFirst = false;
            }
            result += n.abr.debug();
        }
        result += " edges : ";
        isFirst = true;
        for (AbstractDualEdge e : edges) {
            if (!isFirst) {
                result += ",";
            } else {
                isFirst = false;
            }
            result += e.from.abr.debug();
            result += "->";
            result += e.to.abr.debug();
        }
        return result;
    }

    public ArrayList<AbstractDualNode> getFourTuple() {

        for (AbstractDualNode n : nodes) {
            for (AbstractDualEdge e : n.incidentEdges) {
                if (e.from != n) {
                    continue;
                }
                AbstractDualNode n2 = e.to;
                for (AbstractDualEdge e2 : n2.incidentEdges) {
                    if (e2.from != n2) {
                        continue;
                    }

                    // we have edges e and e2 - are these part of a square?
                    logger.debug("edges are " + e.from.abr.debug() + "->" + e.to.abr.debug() + "\n and "
                                + e2.from.abr.debug() + "->" + e2.to.abr.debug());

                    // look for an edge from n with the same label as e2
                    for (AbstractDualEdge e3 : n.incidentEdges) {
                        if (e3.label == e2.label) {
                            // found a square
                            ArrayList<AbstractDualNode> result = new ArrayList<AbstractDualNode>();
                            result.add(n);
                            result.add(n2);
                            result.add(e3.to);
                            result.add(e2.to);
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }
}
