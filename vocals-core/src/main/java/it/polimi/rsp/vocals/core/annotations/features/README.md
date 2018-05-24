A Feature interface must be either

- be annotated with a @Feature annotation at type level
  -  optionally can specify the namespace
- have ONLY one method
  -  each parameter must be annotated with @Param annotation
  -  parameters are default put in the uri
  -  complex classes are flattened in the uri, 
  -  colliding fields/setter between classes may cause erroneous deserializations
- annotate that method with a @RSPService annotation

OR


- have as many methods annotated with @Feature AND @RSPService annotations
- optionally can specify the namespace for the Feature
- UNIQUENESS IS GIVEN BY 

endpoint + uri parameters + method (body param do not count)

@Param annotation to specify  parameters are either put in the uri or the body (default)
 
NB complex classes parameters should go in the body, and will
be flattened. Colliding fields/setter between classes may cause erroneous deserializations
- annotate that method with a 


IDEA

fix the return time to Answer, 
offering a way for the user to specificy a custom
serialization of the response.

default gson.tojson

====

the 