/*
    Copyright (C) 2015,2016,2017  Diversity Arrays Technology, Pty Ltd.
    
    TextToBarcodePDF may be redistributed and may be modified under the terms
    of the GNU General Public License as published by the Free Software
    Foundation, either version 3 of the License, or (at your option)
    any later version.
    
    TextToBarcodePDF is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with TextToBarcodePDF.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.diversityarrays.dartapps.barcode.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.Transformer;

/**
 * 
 * @author Brian Pearce
 *
 * @param <A>
 * @param <B>
 */
public class Pair<A,B> {
	
	static public <T> Pair<T,T>[] createHomogenousPairs(Collection<T> inputs, final String sep) {
		return createHomogenousPairs(inputs, new Transformer<Pair<T,T>,String>() {
			@Override
			public String transform(Pair<T,T> p) {
				return p.first + sep + p.second;
			}
		});
	}
	
	static public <T> Pair<T,T>[] createHomogenousPairs(Collection<T> inputs, Transformer<Pair<T,T>,String> joiner) {
		List<Pair<T,T>> result = new ArrayList<Pair<T,T>>();
		
		List<T> list = new ArrayList<T>(inputs);
		int n = list.size();
		if (n > 1) {
			for (int a = 0; a < n; ++a) {
				T aa = list.get(a);
				for (int b = a+1; b < n; ++b) {
					T bb = list.get(b);
					
					Pair<T,T> pair = new Pair<T,T>(aa, bb, null);
					String name = joiner.transform(pair);
						
					result.add(new Pair<T,T>(aa, bb, name));
					
					pair = new Pair<T,T>(bb, aa, null);
					name = joiner.transform(pair);
					
					result.add(new Pair<T,T>(bb, aa, name));
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		Pair<T,T>[] pairs = (Pair<T,T>[]) Array.newInstance(Pair.class, result.size());
		pairs = result.toArray(pairs);
		
		return pairs;
	}
	
	public final A first;
	public final B second;
	private final String name;
	
	public Pair(A a, B b) {
		this(a, b, "(" + a+" , "+b+")");
	}
	
	public Pair(A a, B b, String name) {
		this.first = a;
		this.second = b;
		this.name = name;
	}
	
	public A getFirst(){
		return this.first;
	}
	

	public B getSecond(){
		return this.second;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (! (o instanceof Pair)) return false;
		
		Pair<?,?> other = (Pair<?,?>) o;
		Object oa = other.first;
		Object ob = other.second;
		
		return (oa == null ? first == null : oa.equals(first))
				&&
				(ob == null ? second == null : ob.equals(second));
	}
	
	@Override
	public int hashCode() {
		return (first == null ? 0 : first.hashCode())
			^
				(second == null ? 0 : second.hashCode());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
