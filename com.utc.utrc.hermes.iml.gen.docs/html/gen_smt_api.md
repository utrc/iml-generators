# IML to SMT Generator: API

`package com.utc.utrc.hermes.iml.gen.smt.encoding;`

Class `ImlSmtEncoder` implements the five APIs specified by interface `ImlEncoder`: 

- `public void encode(Model model)`
- `public void encode(Symbol symbol)`
- `public void encode(SymbolDeclaration symbol)`
- `public void encode(NamedType type)`
- `public void encode(ImlType imlType)`

Given a system consisting of potentially multiple IML files, the contents can be read into memory via the API `readAllFilesUnderDir(String path)` defined in class `FileUtil` in package `com.utc.utrc.hermes.iml.util`. 
For example, `List<String> files = FileUtil.readAllFilesUnderDir("pathString")` reads in content of files in directory pathString. 
Then an in-memory image of the system can be created by parsing the files and adding the their contents to a resource set. 
For example, the following code segment will generate the resource set for `files`. 

    org.eclipse.emf.ecore.resource.ResourceSet rs = null;
    for (final String file : files) {
      if (rs != null) {
        rs = imlParseHelper.parse(file, rs).eResource().getResourceSet();
      } else {
        rs = imlParseHelper.parse(file).eResource().getResourceSet();
      }
    }

`imlParseHelper` is an instance of class `ImlParseHelper` defined in package `com.utc.utrc.hermes.iml`. 

Use the example shown in Section [IML to SMT Generator: Input](./.gen_smt_input.md.html) as an example, the following code segment prepares `swModel` of type `Model` for `smt.example`. 

    Model swModel = null;
    EList<Resource> _resources = rs.getResources();
    for (final Resource resource : _resources) {
      if (((resource.getContents() != null) && (resource.getContents().size() > 0))) {
        final EObject model = resource.getContents().get(0);
        if ((model instanceof Model)) {
          boolean _equals = ((Model) model).getName().equals("smt.example");
          if (_equals) {
            swModel = ((Model)model);
          }
        }
      }
    }

Then the following code segment will encode the `Model` for named type `top_level_dot_Impl`. 

    ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder;
    encoder.encode(findSymbol(swModel, "top_level_dot_Impl"));

The above method `findSymbol` is defined as follows:
    
    public Symbol findSymbol(final Model model, final String name) {
      final Function1<Symbol, Boolean> _function = (Symbol it) -> {
        return Boolean.valueOf(it.getName().equals(name));
      };
      return IterableExtensions.<Symbol>findFirst(model.getSymbols(), _function);
    }    
    
The encoding can be displayed by calling method `toString()` of the `encoder`.

    println(encoder.toString());

Finally, encoding of `Symbol`, `SymbolDeclaration`, `NamedType` and `ImlType` can be obtained by invoking their respective APIs as shown at the beginning of this section.