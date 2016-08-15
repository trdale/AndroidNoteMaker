from google.appengine.ext import ndb
import datetime

class User(ndb.Model):
    uname = ndb.StringProperty(required=True)
    pw = ndb.StringProperty(required=True)
    token = ndb.IntegerProperty(required=True)

class Note(ndb.Model):
	def to_dict(self): 
		d = super(Note, self).to_dict()
		d['key'] = self.key.id()
		return d
	
	user = ndb.IntegerProperty(required=True)
	title = ndb.StringProperty(required=True)
	comment = ndb.StringProperty(required=True)
	long = ndb.StringProperty(required=True)
	lat = ndb.StringProperty(required=True)