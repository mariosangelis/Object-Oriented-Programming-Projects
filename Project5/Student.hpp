#ifndef _STUDENT_HPP_
#define _STUDENT_HPP_

#include<cstring>
#include<string>
#include<iostream>

class Student {
  char *name;
  int aem;
  
public:
  Student(istream& in); 
  Student(const char *name, int aem);
  Student();
  Student(const Student& );
  ~Student();
  char *getName() const;
  int getAEM() const;
  void setName(const char *name);
  void setAEM(int aem);
  friend std::ostream& operator<<(std::ostream& out, const Student & st);
  bool operator>(const Student& st) const;
  Student& operator=(const Student& st);
  bool operator==(const Student& other) const;
  bool operator!=(const Student& other);
};

Student::Student(istream& in) {
  string sname;
  in >> sname;
  
  this->name = new char[sname.size()+1];
  strcpy(this->name, sname.c_str());
  in >> this->aem;
}

Student::Student(const char *name, int aem) {
  if(name != nullptr) {
    this->name = new char [strlen(name) + 1];
    strcpy(this->name, name);
  }
  else
    this->name = nullptr;
  
  this->aem = aem;
}

Student::Student(const Student& st) {
  if(st.name != nullptr) {
    name = new char [strlen(st.name) + 1];
    strcpy(name, st.name);
  }
  else
    name = nullptr;
  aem = st.aem;
}

Student::Student() {
  this->name = nullptr;
  this->aem = 0;
}

Student::~Student() {
  if(name != nullptr)
    delete []name;
}

char* Student::getName() const {
  return name;
}

int Student::getAEM() const {
  return aem;
}


void Student::setName(const char *name) {
  if(this->name != nullptr)
    delete this->name;
  this->name = new char [strlen(name) + 1];
  strcpy(this->name, name);
}

void Student::setAEM(int aem) {
  this->aem = aem;
}

Student& Student::operator=(const Student& st) {
  if(name != nullptr)
    delete name;
  if(st.name != nullptr) {
    name = new char [strlen(st.name) + 1];
    strcpy(name, st.name);
  }
  else
    name = nullptr;
  aem = st.aem;
  return *this;
}

std::ostream& operator<<(std::ostream& out, const Student& st) {
  if(st.name != nullptr)
    out << st.name ; //<< " " << st.aem;
  return out;
}

bool Student::operator>(const Student& st) const {
  if(aem > st.aem)
    return true;
  return false;
}

bool Student::operator==(const Student& other) const{
  return (!strcmp(name, other.name) && aem == other.aem);
}

bool Student::operator!=(const Student& other) {
  return !(*this == other);
}
#endif
