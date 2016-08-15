import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class Note(webapp2.RequestHandler):
	def get(self):
		self.response.status = 200
		self.response.write("yo note")
		
	def post(self):
		token = self.request.get('token', default_value=None)
		if not token:
			self.response.write(json.dumps({'msg': 'must be logged in'}))
			return
		else:
			self.response.write(json.dumps([q.to_dict() for q in db_defs.Note.query(db_defs.Note.user == int(token)).fetch()]))
			return
			
	def delete(self):
		token = self.request.get('token', default_value=None)
		key = self.request.get('id', default_value=None)
		if not token:
			self.response.write(json.dumps({'msg': 'must be logged in'}))
			return
		else:
			q = db_defs.Note.query()
			note = ndb.Key(db_defs.Note, int(key)).get()
			if not note:
				self.response.status = 404;
				self.response.write(json.dumps({'msg': 'Note Not Found'}))
				return
			else:
				note.key.delete()
				self.response.status = 200;
				self.response.write(json.dumps({'msg': 'Note Deleted'}))
				return
	
			
class AddNote(webapp2.RequestHandler):
	def get(self):
		self.response.status = 200;
		self.response.write("YO ADDIN NOTES")
	
	def post(self):
		title = self.request.get('title', default_value=None)
		comment = self.request.get('context', default_value=None)
		user = self.request.get('user', default_value=None)
		long = self.request.get('long', default_value=None)
		lat = self.request.get('lat', default_value=None)
		new_note = db_defs.Note()
		new_note.title = title;
		new_note.comment = comment;
		new_note.user = int(user);
		new_note.long = long;
		new_note.lat = lat;
		new_note.put()
		self.response.write({'msg': new_note})
		return

class EditNote(webapp2.RequestHandler):
	def get(self):
		self.response.status = 200;
		self.response.write("YO EDITIN NOTES")
	
	def put(self):
		title = self.request.get('title', default_value=None)
		comment = self.request.get('context', default_value=None)
		token = self.request.get('user', default_value=None)
		key = self.request.get('id', default_value=None)
		long = self.request.get('long', default_value=None)
		lat = self.request.get('lat', default_value=None)		
		
		if not token:
			self.response.write(json.dumps({'msg': 'must be logged in'}))
			return
		else:
			q = db_defs.Note.query()
			note = ndb.Key(db_defs.Note, int(key)).get()
			if not note:
				self.response.status = 404;
				self.response.write(json.dumps({'msg': 'Note Not Found'}))
				return
			else:
				note.title = title;
				note.comment = comment;
				note.long = long;
				note.late = lat;
				note.put()
				self.response.status = 200;
				self.response.write(json.dumps({'msg': 'Note Updated'}))
				return