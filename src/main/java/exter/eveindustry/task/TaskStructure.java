package exter.eveindustry.task;

import exter.eveindustry.data.structure.Structure;
import exter.eveindustry.data.structure.StructureRig;
import exter.eveindustry.data.structure.StructureService;

public class TaskStructure
{
   public final Structure structure;
   
   private StructureService[] services;
   private StructureRig[] rigs;
   
   public TaskStructure(Structure structure)
   {
     this.structure = structure;
     this.services = new StructureService[structure.service_slots];
     this.rigs = new StructureRig[structure.rig_slots];
   }
   
   public StructureService getService(int slot)
   {
     if(slot < 0 || slot >= services.length)
     {
       return null;
     }
     return this.services[slot];
   }

   public void setService(int slot,StructureService module)
   {
     if(slot >= 0 && slot < services.length)
     {
       this.services[slot] = module;
     }
   }

   public StructureRig getRig(int slot)
   {
     if(slot < 0 || slot >= rigs.length)
     {
       return null;
     }
     return this.rigs[slot];
   }

   public void setRig(int slot,StructureRig module)
   {
     if(slot >= 0 && slot < rigs.length)
     {
       this.rigs[slot] = module;
     }
   }
}
