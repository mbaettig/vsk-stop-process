{ pkgs }:

let
  # Newer nixpkgs only for Java 25
  pkgsNew = import (builtins.fetchTarball "https://github.com/NixOS/nixpkgs/archive/nixpkgs-unstable.tar.gz") {};
in
{
  deps = [
    pkgsNew.jdk25
    pkgsNew.maven
    # Keep these from Replit’s overlay (only works on stable-22_11, etc.)
    pkgs.replitPackages.jdt-language-server
    pkgs.replitPackages.java-debug
  ];
}