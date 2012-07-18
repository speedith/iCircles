package icircles.abstractDescription;
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

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An AbstractSpider stores the name of a spider and the {@link AbstractBasicRegion}
 * it inhabits.
 */
public class AbstractSpider implements Comparable<AbstractSpider>{

	private TreeSet<AbstractBasicRegion> m_feet;
	private String m_name;

	public AbstractSpider(TreeSet<AbstractBasicRegion> feet, String name){
		m_feet = feet;
		m_name = name;
	}

	public SortedSet<AbstractBasicRegion> get_feet() {
		return Collections.unmodifiableSortedSet(m_feet);
	}

	public String getName(){
		return m_name;
	}

        public void setName(String name) {
            this.m_name = name;
        }

	public int compareTo(AbstractSpider other) {

	    if (other.m_feet.size() < m_feet.size()) {
		return 1;
	    } else if (other.m_feet.size() > m_feet.size()) {
		return -1;
	    }

	    // same sized m_feet
	    Iterator<AbstractBasicRegion> this_it = m_feet.iterator();
	    Iterator<AbstractBasicRegion> other_it = other.m_feet.iterator();

	    while (this_it.hasNext()) {
		AbstractBasicRegion this_c = this_it.next();
		AbstractBasicRegion other_c = other_it.next();
		int comp = this_c.compareTo(other_c);
		if (comp != 0) {
		    return comp;
		}
	    }
	    return 0;
	}

	public String journalString() {
	    StringBuilder b = new StringBuilder();
	    for (AbstractBasicRegion z : m_feet) {
		b.append(z.journalString());
		b.append(" ");
	    }
	    if(m_name != null)
		{
		    b.append("'");
		    b.append(m_name);
		}
	    return b.toString();
	}
}
