namespace AppSenSoutenance.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class InitialCreate : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.AnneeAcademiques",
                c => new
                    {
                        IdAnneeAcademique = c.Int(nullable: false, identity: true),
                        LibelleAnneeAcademique = c.String(nullable: false, maxLength: 9, storeType: "nvarchar"),
                        AnneeAcademiqueVal = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.IdAnneeAcademique);
            
        }
        
        public override void Down()
        {
            DropTable("dbo.AnneeAcademiques");
        }
    }
}
