{ pkgs }:

let
  # Pinned nixpkgs (for JDK 25) – same result on every import
  pkgsNew = import (builtins.fetchTarball
    "https://github.com/NixOS/nixpkgs/archive/c7def046b9a883d46974757852106483d741586f.tar.gz") {};
in
{
  deps = [
    pkgsNew.jdk25
    pkgsNew.maven
    pkgsNew.jdt-language-server
  ];
}

