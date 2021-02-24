# IML to Lustre Generator: API

`package com.utc.utrc.hermes.iml.gen.lustre.df.generator;`

Class `LustreGenerator` provides `public String serialize(LustreModel m)`, which utilizes services offered by class `LustreGeneratorServices` to emit Lustre encoding for programs in IML.

In the rest of this section, the example shown in Section [IML to Lustre Generator: Input](./.gen_lustre_input.md.html) is used to illustrate the steps of producing Lustre encoding. 
Assume the IML program is in a single file whose path is `pathString`. The following code reads in the content of an IML file. 

    CharSequence mt = FileUtil.readFileContent("pathString");

For systems consisting of multiple IML files, please follow what is discussed in Section [IML to SMT Generator: API](./.gen_smt_api.md.html).

Next an in-memory image of the system can be created by parsing the content of the file. 
For example, the following code segment will generate a synchronous dataflow system and attach it to a Lustre generator. 

    ImlParseHelper imlParseHelper;
    SystemsServices systemServices;
    IQualifiedNameProvider qnp;
    SynchDf sdf;
    LustreGenerator gen;
    
    final Systems sys = new Systems(systemServices, qnp);
    Model m = imlParseHelper.parse(mt, true);
    sys.process(m);
    sdf.setSystems(sys);
    sdf.process(m);
    gen.setSdf(sdf);
    
At this point, `LustreGenerator` `gen` has been furnished with a dataflow model. 
The following code segment illustrates how to construct a Lustre model. 
    
    Symbol _findSymbol = findSymbol(m, "top_level_dot_Impl");
    NamedType nodetype = ((NamedType) _findSymbol);
    LustreModel lus = new LustreModel();
    gen.generateLustreNode(lus, sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype)));
    
Refer to Section [IML to SMT Generator: API](./.gen_smt_api.md.html) for the definition of method `findSymbol`.     
    
Now a Lustre model for the constrained type `top_level_dot_Impl` has been generated. Finally its Lustre encoding can by obtained as follows:  
    
    String output = gen.serialize(lus);