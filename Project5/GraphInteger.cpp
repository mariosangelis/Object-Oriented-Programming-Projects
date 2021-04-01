
#include <cstdlib>
#include <sstream>

using namespace std;

class Integer {
	int value;
public:
	Integer() {
		value = int();
	}
	Integer(istream& in) {
		in >> value;
	}
	int& get() {
		return value;
	}
	friend ostream& operator<<(ostream& out, const Integer& myint) {
		out << myint.value;
		return out;
	}
	bool operator==(const Integer& other) const{
		return value == other.value;
	}
	bool operator!=(const Integer& other) const{
		return(! (value == other.value));
	}
	
};

#include "GraphUI.hpp"

int main() {
	return graphUI<Integer>();
}
